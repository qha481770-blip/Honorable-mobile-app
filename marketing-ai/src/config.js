import path from 'node:path';

export function config(env = process.env, cwd = process.cwd()) {
  return {
    host: env.HOST || '127.0.0.1',
    port: Number(env.PORT || 4173),
    publicBaseUrl: env.PUBLIC_BASE_URL || 'http://127.0.0.1:4173',
    dataDir: path.resolve(cwd, env.DATA_DIR || './data'),
    llmProvider: env.MARKETING_LLM_PROVIDER || 'local-rules',
    emailProvider: env.EMAIL_PROVIDER || 'disabled',
    credentials: {
      tiktok: env.TIKTOK_ACCESS_TOKEN,
      instagram: env.INSTAGRAM_ACCESS_TOKEN,
      youtube: env.YOUTUBE_API_KEY,
      reddit: env.REDDIT_CLIENT_ID && env.REDDIT_CLIENT_SECRET,
      googleTrends: env.GOOGLE_TRENDS_PROVIDER_KEY,
      email: env.EMAIL_API_KEY && env.EMAIL_FROM
    }
  };
}
