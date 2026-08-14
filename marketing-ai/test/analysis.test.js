import test from 'node:test';
import assert from 'node:assert/strict';
import { IntentClassifier, OpportunityAnalyzer } from '../src/core/analysis.js';
import { ContentGenerator, LocalRulesMarketingModel } from '../src/core/content.js';
import { createCollectors, ConnectorStatus } from '../src/core/platforms.js';

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
  assert.equal(reports.find(x=>x.platform==='TikTok').status,ConnectorStatus.NOT_CONFIGURED);
  assert.ok(reports.every(x=>x.autoPublish===false));
});

test('credentials never claim CONNECTED before an official client is implemented', () => {
  const report=createCollectors({tiktok:'token'}).find(x=>x.name==='TikTok').report();
  assert.equal(report.status,ConnectorStatus.UNSUPPORTED);
});
