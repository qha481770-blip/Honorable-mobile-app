const painRules = [
  ['SCREENSHOT', /screenshot/i], ['VIDEO', /video|clip|footage/i], ['PRIVACY', /private|privacy|upload|cloud/i],
  ['STORAGE', /storage|space|gigabyte/i], ['ORGANIZATION', /organi[sz]|album|folder|sort/i],
  ['AI', /\bai\b|artificial intelligence/i], ['SEARCH', /find|search|remember|scroll/i]
];

export class IntentClassifier {
  classify(text) {
    const value = text.toLowerCase();
    const high = /(can never find|can't find|cannot find|need one|how do .* find|search .* photos|search .* videos|does not upload)/i.test(value);
    const medium = /(best way|organize|sort|manage|too many|advice)/i.test(value);
    return high ? 'HIGH INTENT' : medium ? 'MEDIUM INTENT' : 'LOW INTENT';
  }
  painPoint(text) { return painRules.find(([, rule]) => rule.test(text))?.[0] || 'OTHER'; }
}

export class OpportunityAnalyzer {
  constructor(classifier = new IntentClassifier(), now = () => new Date()) { this.classifier = classifier; this.now = now; }
  analyze(item) {
    const intent = this.classifier.classify(`${item.topic} ${item.text}`);
    const painPoint = this.classifier.painPoint(`${item.topic} ${item.text}`);
    const relevance = /find|search|scroll|remember|organi[sz]|photo|video|screenshot|private/i.test(`${item.topic} ${item.text}`) ? .92 : .35;
    const intentValue = { 'HIGH INTENT':1, 'MEDIUM INTENT':.65, 'LOW INTENT':.25 }[intent];
    const ageDays = Math.max(0, (this.now() - new Date(item.publishedAt)) / 86400000);
    const recency = Math.max(.15, 1 - ageDays / 30);
    const engagement = Math.min(1, Math.log10((item.engagement || 0) + 1) / 3);
    const platform = ({ Reddit:.95, YouTube:.9, TikTok:.86, Instagram:.78 }[item.platform] || .65);
    const score = Math.round(100 * (relevance*.30 + intentValue*.25 + recency*.15 + engagement*.10 + platform*.08 + (item.audienceFit || .5)*.12));
    return { ...item, intent, painPoint, score, scoring:{ problemRelevance:relevance, intent:intentValue, recency, engagement, platform, audienceFit:item.audienceFit || .5 }, angle:this.angle(painPoint, intent) };
  }
  angle(pain, intent) {
    const angles = { SEARCH:'Show one natural-language query replacing endless scrolling.', VIDEO:'Demo finding a precise remembered video moment.', SCREENSHOT:'Show finding a screenshot from the words or scene remembered.', PRIVACY:'Lead with on-device search and “Your memories stay yours.”', ORGANIZATION:'Position search as an alternative to maintaining albums.', AI:'Make local, useful AI tangible through a real retrieval.', STORAGE:'Focus on navigating a large existing library without uploading it.', OTHER:'Demonstrate the remembered-moment workflow.' };
    return `${angles[pain]} ${intent === 'HIGH INTENT' ? 'Use a direct waitlist CTA.' : 'Lead with education before the CTA.'}`;
  }
}

export class TrendAnalyzer {
  summarize(opportunities) {
    const counts = opportunities.reduce((all, x) => ((all[x.painPoint] = (all[x.painPoint] || 0) + 1), all), {});
    return Object.entries(counts).sort((a,b) => b[1]-a[1]).map(([painPoint,count]) => ({ painPoint,count,share:Math.round(count/opportunities.length*100) || 0 }));
  }
}
