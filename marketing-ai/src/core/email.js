export class EmailAdapter {
  constructor(configured = false) { this.configured = configured; }
  status() { return this.configured ? 'CONNECTED — SENDING REQUIRES EXPLICIT ACTION' : 'NOT CONFIGURED — TEMPLATES READY'; }
  welcome(email) { return { to:email, subject:"You're on the Honorable waitlist", text:'You joined Honorable early. We’ll let you know when private early access becomes available. Your memories stay yours.' }; }
  betaInvitation(email) { return { to:email, subject:'Your Honorable beta invitation', text:'Private early access is ready. Follow the invitation instructions when you are ready to try Honorable.' }; }
  launchAnnouncement(email) { return { to:email, subject:'Honorable is ready', text:'You remember the moment. Honorable finds it. Honorable is now available.' }; }
  async send() { throw new Error('Automatic email sending is disabled'); }
}
