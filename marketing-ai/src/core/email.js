export class EmailService {
  status() { return 'ABSTRACT'; }
  async sendWelcome() { throw new Error('Not implemented'); }
}

export class TestEmailService extends EmailService {
  constructor() { super(); this.deliveries=[]; }
  status() { return 'TEST MODE — NO EMAIL SENT'; }
  async sendWelcome(message) { this.deliveries.push(message); return {status:'simulated',providerId:null}; }
}

export class ResendEmailService extends EmailService {
  constructor({apiKey,from,fetchImpl=fetch}) { super(); this.apiKey=apiKey; this.from=from; this.fetch=fetchImpl; }
  status() { return this.apiKey&&this.from?'CONFIGURED — RESEND':'CONFIGURATION REQUIRED'; }
  async sendWelcome({to,unsubscribeUrl}) {
    if (!this.apiKey||!this.from) throw new Error('Resend is not configured');
    const response=await this.fetch('https://api.resend.com/emails',{method:'POST',headers:{authorization:`Bearer ${this.apiKey}`,'content-type':'application/json'},body:JSON.stringify({
      from:this.from,to:[to],subject:"You're on the Honorable waitlist",
      text:`You’re in. You remember the moment. Honorable finds it.\n\nWe’ll email you when private early access is ready. Your memories stay yours—Honorable is built with privacy at its core.\n\nUnsubscribe: ${unsubscribeUrl}`,
      html:`<p>You’re in.</p><p><strong>You remember the moment. Honorable finds it.</strong></p><p>We’ll email you when private early access is ready. Your memories stay yours—Honorable is built with privacy at its core.</p><p><a href="${unsubscribeUrl}">Unsubscribe</a></p>`
    })});
    const result=await response.json().catch(()=>({}));
    if (!response.ok) { const error=new Error('Email provider rejected the request'); error.code=String(result.name||response.status); throw error; }
    return {status:'success',providerId:result.id};
  }
}

export function createEmailService(settings, dependencies={}) {
  if (dependencies.emailService) return dependencies.emailService;
  return settings.emailProvider==='resend' ? new ResendEmailService({apiKey:settings.emailApiKey,from:settings.emailFrom}) : new TestEmailService();
}
