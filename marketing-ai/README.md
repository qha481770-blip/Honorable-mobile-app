# Honorable Marketing AI

A separate, local-first pre-launch marketing system. It does not import or modify Memories AI, TinyCLIP, VLM search, iOS search, subscriptions, or authentication.

## Run

```bash
cd marketing-ai
cp .env.example .env # optional; the defaults work without it
npm start
```

- Waitlist website: `http://127.0.0.1:4173/`
- Marketing dashboard: `http://127.0.0.1:4173/dashboard`

Run tests with `npm test`.

## Safety and connector policy

- All bundled opportunities are explicitly labeled mock data.
- Platform adapters report `NOT CONFIGURED` until official credentials exist.
- No scraping, automatic DMs, comment spam, email harvesting, fingerprinting, or platform bypasses exist.
- Content generation creates drafts only. Approval marks a draft ready for manual publishing; it never publishes.
- Email templates exist, but delivery is disabled and requires a separate provider adapter plus explicit action.
- Waitlist state is stored in `data/marketing-state.json` with mode `0600` and ignored by Git.
- API secrets belong in environment variables; `.env` is ignored.

The current content generator uses deterministic local rules, so no paid AI API is required. `MarketingLanguageModel` can be replaced later without changing campaign workflows.
