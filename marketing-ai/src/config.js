import path from 'node:path';

export function config(env = process.env, cwd = process.cwd()) {
  return {
    host: env.HOST || '127.0.0.1',
    port: Number(env.PORT || 4173),
    publicBaseUrl: env.PUBLIC_BASE_URL || 'http://127.0.0.1:4173',
    dataDir: path.resolve(cwd, env.DATA_DIR || './data'),
    llmProvider: env.MARKETING_LLM_PROVIDER || 'local-rules',
    waitlistStore: env.WAITLIST_STORE || 'local-json',
    databaseUrl: env.DATABASE_URL,
    emailProvider: env.EMAIL_PROVIDER || 'test',
    emailApiKey: env.RESEND_API_KEY,
    emailFrom: env.EMAIL_FROM,
    discoveryMode: env.DISCOVERY_MODE || 'mock',
    webSearch: { apiKey:env.GOOGLE_SEARCH_API_KEY, engineId:env.GOOGLE_SEARCH_ENGINE_ID },
    credentials: {
      tiktok: env.TIKTOK_ACCESS_TOKEN,
      instagram: env.INSTAGRAM_ACCESS_TOKEN && env.INSTAGRAM_BUSINESS_ACCOUNT_ID ? { token:env.INSTAGRAM_ACCESS_TOKEN, accountId:env.INSTAGRAM_BUSINESS_ACCOUNT_ID } : null,
      youtube: env.YOUTUBE_API_KEY,
      reddit: env.REDDIT_CLIENT_ID && env.REDDIT_CLIENT_SECRET && env.REDDIT_COMMERCIAL_APPROVAL === 'true' ? { clientId:env.REDDIT_CLIENT_ID, clientSecret:env.REDDIT_CLIENT_SECRET, userAgent:env.REDDIT_USER_AGENT } : null,
      redditApproval: env.REDDIT_COMMERCIAL_APPROVAL === 'true',
      googleTrends: env.GOOGLE_TRENDS_PROVIDER_KEY,
      email: env.RESEND_API_KEY && env.EMAIL_FROM
    }
  };
}
