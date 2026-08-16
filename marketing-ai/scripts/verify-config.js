import { databaseStatus } from './database-check.js';

const timeoutMs=8000;
const timeoutSignal=()=>AbortSignal.timeout(timeoutMs);

async function requestStatus(required,request) {
  if (required.some(value=>!value)) return 'MISSING';
  try { const response=await request(); return response.ok?'CONNECTED':'FAILED'; } catch { return 'FAILED'; }
}

export async function verifyConfig(env=process.env,fetchImpl=fetch) {
  const database=await databaseStatus(env);
  const email=await requestStatus([env.RESEND_API_KEY,env.EMAIL_FROM],()=>fetchImpl('https://api.resend.com/domains',{headers:{authorization:`Bearer ${env.RESEND_API_KEY}`},signal:timeoutSignal()}));
  const youtube=await requestStatus([env.YOUTUBE_API_KEY],()=>{const url=new URL('https://www.googleapis.com/youtube/v3/videos');url.search=new URLSearchParams({part:'id',id:'dQw4w9WgXcQ',key:env.YOUTUBE_API_KEY}).toString();return fetchImpl(url,{signal:timeoutSignal()});});
  const web=await requestStatus([env.GOOGLE_SEARCH_API_KEY,env.GOOGLE_SEARCH_ENGINE_ID],()=>{const url=new URL('https://www.googleapis.com/customsearch/v1');url.search=new URLSearchParams({key:env.GOOGLE_SEARCH_API_KEY,cx:env.GOOGLE_SEARCH_ENGINE_ID,q:'Honorable configuration check',num:'1'}).toString();return fetchImpl(url,{signal:timeoutSignal()});});
  const instagram=env.INSTAGRAM_ACCESS_TOKEN&&env.INSTAGRAM_BUSINESS_ACCOUNT_ID?'CONFIGURED':'APPROVAL REQUIRED';
  const reddit=env.REDDIT_COMMERCIAL_APPROVAL==='true'&&env.REDDIT_CLIENT_ID&&env.REDDIT_CLIENT_SECRET&&env.REDDIT_USER_AGENT?'CONFIGURED':'APPROVAL REQUIRED';
  return {database,email,youtube,web,instagram,reddit,tiktok:'UNAVAILABLE'};
}

const status=await verifyConfig();
console.log(`DATABASE: ${status.database.status}`);
if(status.database.reason)console.log(`REASON: ${status.database.reason}`);
console.log(`EMAIL: ${status.email}`);
console.log(`YOUTUBE: ${status.youtube}`);
console.log(`WEB DISCOVERY: ${status.web}`);
console.log(`INSTAGRAM: ${status.instagram}`);
console.log(`REDDIT: ${status.reddit}`);
console.log(`TIKTOK: ${status.tiktok}`);
