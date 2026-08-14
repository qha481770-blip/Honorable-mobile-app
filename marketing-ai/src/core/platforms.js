export const ConnectorStatus = Object.freeze({
  CONNECTED: 'CONNECTED',
  NOT_CONFIGURED: 'NOT CONFIGURED',
  UNSUPPORTED: 'UNSUPPORTED',
  MANUAL: 'MANUAL / UNAVAILABLE'
});

export class PlatformCollector {
  constructor(name, configured = false, access = 'official-api', implemented = false) {
    this.name = name;
    this.configured = Boolean(configured);
    this.access = access;
    this.implemented = implemented;
  }
  status() { return !this.configured ? ConnectorStatus.NOT_CONFIGURED : this.implemented ? ConnectorStatus.CONNECTED : ConnectorStatus.UNSUPPORTED; }
  async collectPublicOpportunities() {
    if (!this.configured) return [];
    throw new Error(`${this.name} official API client is not implemented yet`);
  }
  report() { return { platform: this.name, status: this.status(), access: this.access, autoPublish: false }; }
}

export class ManualCollector extends PlatformCollector {
  status() { return ConnectorStatus.MANUAL; }
  async collectPublicOpportunities() { return []; }
}

export function createCollectors(credentials = {}) {
  return [
    new PlatformCollector('TikTok', credentials.tiktok),
    new PlatformCollector('Instagram', credentials.instagram),
    new PlatformCollector('YouTube', credentials.youtube),
    new PlatformCollector('Reddit', credentials.reddit),
    new PlatformCollector('Google Trends', credentials.googleTrends, 'authorized-provider'),
    new ManualCollector('Other public sources', false, 'manual-review-only')
  ];
}

export const mockPublicConversations = [
  { id:'mock-1', platform:'Reddit', topic:'I have 30,000 photos and can never find anything', text:'I have 30,000 photos and can never find the old beach photo I remember.', sourceUrl:'https://example.invalid/mock/reddit-1', publishedAt:'2026-08-11T12:00:00Z', engagement:420, audienceFit:.96, mock:true },
  { id:'mock-2', platform:'YouTube', topic:'Searching years of family videos', text:'How do people find one moment in years of videos without remembering the date?', sourceUrl:'https://example.invalid/mock/youtube-1', publishedAt:'2026-08-09T16:30:00Z', engagement:176, audienceFit:.91, mock:true },
  { id:'mock-3', platform:'TikTok', topic:'Endless screenshot scrolling', text:'POV you know the screenshot is on your phone but search cannot find the words.', sourceUrl:'https://example.invalid/mock/tiktok-1', publishedAt:'2026-08-12T08:20:00Z', engagement:880, audienceFit:.87, mock:true },
  { id:'mock-4', platform:'Instagram', topic:'Photo organization advice', text:'What is the best way to organize iPhone photos when albums become another chore?', sourceUrl:'https://example.invalid/mock/instagram-1', publishedAt:'2026-08-07T10:10:00Z', engagement:95, audienceFit:.72, mock:true },
  { id:'mock-5', platform:'Reddit', topic:'Private AI photo search', text:'Is there an AI photo search that stays on device and does not upload my library?', sourceUrl:'https://example.invalid/mock/reddit-2', publishedAt:'2026-08-13T09:00:00Z', engagement:244, audienceFit:.98, mock:true }
];
