import test from 'node:test';
import assert from 'node:assert/strict';
import { IntentClassifier, OpportunityAnalyzer } from '../src/core/analysis.js';
import { ContentGenerator, LocalRulesMarketingModel } from '../src/core/content.js';
import { createCollectors, ConnectorStatus, YouTubeMarketingCollector } from '../src/core/platforms.js';
import { TestEmailService, ResendEmailService } from '../src/core/email.js';

test('intent examples classify as specified', () => {
  const classifier=new IntentClassifier();
  assert.equal(classifier.classify('I have 30,000 photos and can never find anything'),'HIGH INTENT');
  assert.equal(classifier.classify("What's the best way to organize iPhone photos?"),'MEDIUM INTENT');
  assert.equal(classifier.classify('I love photography'),'LOW INTENT');
  assert.equal(classifier.painPoint('I cannot find the old video'),'VIDEO');
  assert.equal(classifier.painPoint('AI search without uploading my library'),'PRIVACY');
});

test('opportunity score is bounded and exposes every score component', () => {
  const analyzer=new OpportunityAnalyzer(undefined,()=>new Date('2026-08-14T00:00:00Z'));
  const result=analyzer.analyze({platform:'Reddit',topic:'Cannot find photo',text:'I can never find one old photo',publishedAt:'2026-08-13T00:00:00Z',engagement:100,audienceFit:.9});
  assert.ok(result.score>=0&&result.score<=100);
  assert.deepEqual(Object.keys(result.scoring),['problemRelevance','intent','recency','engagement','platform','audienceFit']);
});

test('local content generator creates an approval-required draft without fake claims', async () => {
  const draft=await new ContentGenerator(new LocalRulesMarketingModel()).generate({format:'TikTok',opportunity:{painPoint:'SEARCH'}});
  assert.equal(draft.status,'DRAFT');assert.match(draft.cta,/You remember the moment/);assert.match(draft.claims,/No testimonial/);
});

test('platforms report NOT CONFIGURED without official credentials', () => {
  const reports=createCollectors({}).map(x=>x.report());
  assert.equal(reports.find(x=>x.platform==='YouTube').status,ConnectorStatus.NOT_CONFIGURED);
  assert.equal(reports.find(x=>x.platform==='Instagram').status,ConnectorStatus.CONFIGURATION_REQUIRED);
  assert.equal(reports.find(x=>x.platform==='Reddit').status,ConnectorStatus.APPROVAL_REQUIRED);
  assert.equal(reports.find(x=>x.platform==='TikTok').status,ConnectorStatus.OFFICIAL_UNAVAILABLE);
  assert.ok(reports.every(x=>x.autoPublish===false));
});

test('configured official collector labels returned public metadata LIVE', async () => {
  const responses=[{items:[{id:{videoId:'abc'},snippet:{title:'Find old photo',description:'cannot find it',channelTitle:'Channel',publishedAt:'2026-08-13T00:00:00Z'}}]},{items:[{id:'abc',statistics:{viewCount:'42'}}]}];
  const collector=new YouTubeMarketingCollector('key',async()=>({ok:true,json:async()=>responses.shift()}));
  const items=await collector.collectPublicOpportunities();assert.equal(items[0].dataType,'LIVE');assert.equal(items[0].engagement,42);assert.match(items[0].sourceUrl,/youtube/);
});

test('official adapter failure is surfaced without a fallback request', async () => {
  const collector=new YouTubeMarketingCollector('key',async()=>({ok:false,status:403}));
  await assert.rejects(()=>collector.collectPublicOpportunities(),/YouTube API 403/);
});

test('email abstraction has a non-sending test mode and Resend request seam', async () => {
  const testEmail=new TestEmailService();const simulated=await testEmail.sendWelcome({to:'person@example.com'});assert.equal(simulated.status,'simulated');assert.equal(testEmail.deliveries.length,1);
  let request;const resend=new ResendEmailService({apiKey:'test-key',from:'Honorable <hello@example.com>',fetchImpl:async(url,options)=>(request={url,options},{ok:true,json:async()=>({id:'email-1'})})});const sent=await resend.sendWelcome({to:'person@example.com',unsubscribeUrl:'https://example.com/unsubscribe?token=x'});assert.equal(sent.status,'success');assert.match(request.options.body,/unsubscribe/);
});
