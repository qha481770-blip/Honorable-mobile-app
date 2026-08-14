import { readFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import crypto from 'node:crypto';
import { JsonStore, publicAnalytics } from './store.js';
import { createCollectors, mockPublicConversations } from './core/platforms.js';
import { IntentClassifier, OpportunityAnalyzer, TrendAnalyzer } from './core/analysis.js';
import { ContentGenerator, LocalRulesMarketingModel } from './core/content.js';
import { CampaignManager } from './core/campaigns.js';
import { EmailAdapter } from './core/email.js';

const publicDir = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../public');
const contentTypes={'.html':'text/html; charset=utf-8','.css':'text/css; charset=utf-8','.js':'text/javascript; charset=utf-8','.svg':'image/svg+xml'};

class RateLimiter {
  constructor(limit=12,windowMs=60000){this.limit=limit;this.windowMs=windowMs;this.hits=new Map();}
  allow(key){const now=Date.now();const current=(this.hits.get(key)||[]).filter(x=>now-x<this.windowMs);current.push(now);this.hits.set(key,current);return current.length<=this.limit;}
}

function json(res,status,body){res.writeHead(status,{'content-type':'application/json; charset=utf-8'});res.end(JSON.stringify(body));}
async function body(req){let raw='';for await(const chunk of req){raw+=chunk;if(raw.length>32768)throw new Error('Request too large');}return raw?JSON.parse(raw):{};}
function cleanAttribution(value,fallback){return /^[a-z0-9_.-]{1,80}$/i.test(value||'')?value:fallback;}
function validEmail(value){return typeof value==='string'&&value.length<=254&&/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);}

export function createMarketingApp(config, dependencies={}) {
  const store=dependencies.store||new JsonStore(config.dataDir);
  const collectors=createCollectors(config.credentials);
  const analyzer=new OpportunityAnalyzer(new IntentClassifier());
  const trends=new TrendAnalyzer();
  const generator=new ContentGenerator(new LocalRulesMarketingModel());
  const campaigns=new CampaignManager(store);
  const email=new EmailAdapter(Boolean(config.credentials.email));
  const limiter=new RateLimiter();
  const mockOpportunities=mockPublicConversations.map(x=>analyzer.analyze(x)).sort((a,b)=>b.score-a.score);

  async function dashboard() {
    const state=await store.snapshot();
    return { mockData:true,mockDataLabel:'MOCK DATA — NOT GENUINE PLATFORM DISCOVERY',connectors:collectors.map(x=>x.report()),opportunities:mockOpportunities,trends:trends.summarize(mockOpportunities),audiences:[
      {segment:'Overwhelmed large-library owners',signal:'Thousands of photos and repeated failed retrieval'},
      {segment:'Family memory keepers',signal:'Recall the scene or person but not the date'},
      {segment:'Video-heavy creators and parents',signal:'Need one moment inside long or numerous videos'},
      {segment:'Privacy-conscious AI users',signal:'Want useful search without uploading their library'}
    ],alternatives:[
      {name:'Manual albums and folders',insight:'High organization effort; position Honorable around retrieval without upkeep.'},
      {name:'Date and location search',insight:'Useful only when people remember metadata; demonstrate scene-first descriptions.'},
      {name:'Cloud-first visual search',insight:'Lead with the differentiated local/private architecture without making unsupported competitor claims.'},
      {name:'Endless camera-roll scrolling',insight:'The default behavior to replace; make time saved visually obvious without inventing metrics.'}
    ],drafts:state.drafts,campaigns:state.campaigns,analytics:publicAnalytics(state),publishing:campaigns.publishingStatus(),email:email.status(),llm:'LOCAL RULES — NO PAID API' };
  }

  return async function handler(req,res) {
    const requestId=crypto.randomUUID();
    res.setHeader('x-request-id',requestId);res.setHeader('x-content-type-options','nosniff');res.setHeader('referrer-policy','strict-origin-when-cross-origin');res.setHeader('permissions-policy','camera=(), microphone=(), geolocation=()');res.setHeader('content-security-policy',"default-src 'self'; style-src 'self'; script-src 'self'; img-src 'self' data:; connect-src 'self'; frame-ancestors 'none'; base-uri 'none'; form-action 'self'");
    try {
      const url=new URL(req.url,config.publicBaseUrl);
      const key=req.socket?.remoteAddress||'local';
      if(req.method==='GET'&&url.pathname==='/api/dashboard')return json(res,200,await dashboard());
      if(req.method==='GET'&&url.pathname==='/api/status')return json(res,200,{ok:true,system:'Honorable Marketing AI',memoriesAiModified:false,autoPosting:false});
      if(req.method==='POST'&&url.pathname==='/api/events'){
        if(!limiter.allow(`event:${key}`))return json(res,429,{error:'Rate limit reached'});const input=await body(req);if(input.website)return json(res,202,{ok:true});
        if(!['landing_view','waitlist_cta'].includes(input.type))return json(res,400,{error:'Unsupported event'});
        await store.addEvent({type:input.type,source:cleanAttribution(input.source,'direct'),campaign:cleanAttribution(input.campaign,'organic'),content:cleanAttribution(input.content,'unknown')});return json(res,201,{ok:true});
      }
      if(req.method==='POST'&&url.pathname==='/api/waitlist'){
        if(!limiter.allow(`waitlist:${key}`))return json(res,429,{error:'Please wait before trying again'});const input=await body(req);if(input.website)return json(res,202,{ok:true});
        if(!validEmail(input.email))return json(res,400,{error:'Enter a valid email address'});if(input.consent!==true)return json(res,400,{error:'Consent is required'});if(!['Android','iPhone','Both'].includes(input.device))return json(res,400,{error:'Choose Android, iPhone, or Both'});
        const result=await store.addWaitlist({email:input.email,device:input.device,consent:true,source:cleanAttribution(input.source,'direct'),campaign:cleanAttribution(input.campaign,'organic'),content:cleanAttribution(input.content,'unknown')});
        return json(res,result.duplicate?200:201,{ok:true,duplicate:result.duplicate,message:result.duplicate?'You are already on the waitlist.':'You’re on the Honorable waitlist.',referralCode:result.member.referralCode,emailDelivery:email.status()});
      }
      if(req.method==='POST'&&url.pathname==='/api/unsubscribe'){const input=await body(req);return json(res,await store.unsubscribe(input.token)?200:404,{ok:true,message:'Email consent has been withdrawn.'});}
      if(req.method==='POST'&&url.pathname==='/api/content/drafts'){const input=await body(req);const opportunity=mockOpportunities.find(x=>x.id===input.opportunityId)||mockOpportunities[0];const draft=await generator.generate({format:input.format,opportunity});return json(res,201,await store.addDraft({...draft,opportunityId:opportunity.id,mockSource:true}));}
      if(req.method==='POST'&&url.pathname.match(/^\/api\/content\/drafts\/[^/]+\/approve$/)){const id=url.pathname.split('/')[4];const draft=await campaigns.approveDraft(id);return json(res,draft?200:404,draft||{error:'Draft not found'});}
      if(req.method==='POST'&&url.pathname==='/api/campaigns'){const created=await campaigns.create(await body(req));return json(res,201,created);}
      if(req.method!=='GET')return json(res,404,{error:'Not found'});
      const requested=url.pathname==='/'?'index.html':url.pathname==='/dashboard'?'dashboard.html':url.pathname.slice(1);
      const resolved=path.resolve(publicDir,requested);if(!resolved.startsWith(`${publicDir}${path.sep}`))return json(res,403,{error:'Forbidden'});
      const data=await readFile(resolved);res.writeHead(200,{'content-type':contentTypes[path.extname(resolved)]||'application/octet-stream','cache-control':'no-store'});res.end(data);
    } catch(error){if(error.code==='ENOENT')return json(res,404,{error:'Not found'});json(res,error instanceof SyntaxError?400:500,{error:error instanceof SyntaxError?'Invalid JSON':'Request failed',requestId});}
  };
}
