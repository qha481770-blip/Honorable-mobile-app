import http from 'node:http';
import { config } from './config.js';
import { createMarketingApp } from './app.js';

const settings=config();
const server=http.createServer(createMarketingApp(settings));
server.listen(settings.port,settings.host,()=>{
  console.log(`Honorable waitlist: ${settings.publicBaseUrl}/`);
  console.log(`Marketing dashboard: ${settings.publicBaseUrl}/dashboard`);
});
