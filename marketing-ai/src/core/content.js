export class MarketingLanguageModel {
  status() { return 'ABSTRACT'; }
  async generate() { throw new Error('MarketingLanguageModel not configured'); }
}

export class LocalRulesMarketingModel extends MarketingLanguageModel {
  status() { return 'LOCAL RULES'; }
  async generate({ format='TikTok', opportunity }) {
    const pain = opportunity?.painPoint || 'SEARCH';
    const hook = ({ VIDEO:'POV: You remember the exact moment—but not which video.', SCREENSHOT:'You know the screenshot exists. You just cannot find it.', PRIVACY:'What if photo search never needed your photos in the cloud?', ORGANIZATION:'Stop organizing 20,000 photos just to find one.', SEARCH:'POV: You have 28,000 photos and need one from years ago.' }[pain] || 'You remember the moment. Now find it.');
    return { format, hook, beats:[
      'Show the real problem: endless scrolling through a large library.',
      'Type a remembered description: “my son playing tennis outside wearing blue.”',
      'Reveal the strongest real photo or video match.',
      'Close on privacy: “Your memories stay yours.”'
    ], cta:'You remember the moment. Honorable finds it. Join the waitlist.', claims:'Use only validated local search behavior. No testimonial or unvalidated performance claim.', status:'DRAFT', generatedBy:'local-rules' };
  }
}

export class ContentGenerator {
  constructor(model = new LocalRulesMarketingModel()) { this.model = model; }
  generate(input) { return this.model.generate(input); }
}
