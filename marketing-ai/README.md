# Honorable Marketing AI

A separate pre-launch marketing system with safe local defaults and opt-in production integrations. It does not import or modify Memories AI, TinyCLIP, VLM search, iOS search, subscriptions, or authentication.

## Run

```bash
cd marketing-ai
cp .env.example .env # optional; the defaults work without it
npm start
```

- Waitlist website: `http://127.0.0.1:4173/`
- Marketing dashboard: `http://127.0.0.1:4173/dashboard`

Run tests with `npm test`.

## Production configuration

- Set `WAITLIST_STORE=postgres` and `DATABASE_URL` for the production PostgreSQL repository. Tables and unique email constraints are initialized automatically. Local JSON is development fallback only.
- Set `EMAIL_PROVIDER=resend`, `RESEND_API_KEY`, `EMAIL_FROM`, and the externally reachable `PUBLIC_BASE_URL` for welcome delivery and unsubscribe links. Tests always inject a non-sending service.
- Set `DISCOVERY_MODE=live` plus official provider credentials. Collection occurs only when an operator presses **Refresh official APIs**; it never posts, comments, messages, or downloads media.
- Never commit `.env`. Use the deployment platform's encrypted secret storage.

See [PLATFORM-COMPLIANCE.md](./PLATFORM-COMPLIANCE.md) for permissions, approvals, and quota notes.

## Safety and connector policy

- All bundled opportunities are explicitly labeled mock data.
- Platform adapters do not claim `CONNECTED` unless required credentials and approvals are configured. A refresh error is displayed without exposing secrets.
- No scraping, automatic DMs, comment spam, email harvesting, fingerprinting, or platform bypasses exist.
- Content generation creates drafts only. Approval marks a draft ready for manual publishing; it never publishes.
- Resend delivery is available only when explicitly selected; default/test mode never sends real email.
- Development waitlist state is stored in `data/marketing-state.json` with mode `0600` and ignored by Git. Production uses PostgreSQL.
- API secrets belong in environment variables; `.env` is ignored.

The current content generator uses deterministic local rules, so no paid AI API is required. `MarketingLanguageModel` can be replaced later without changing campaign workflows.
