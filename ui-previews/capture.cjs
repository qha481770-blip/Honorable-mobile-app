const { chromium } = require('playwright');
const path = require('path');
const fs = require('fs');
const ids=['01-home','02-memories','03-search-focus','04-ai-searching','05-search-results','06-video-results','07-media-viewer','08-terms-ai','09-terms-result','10-activity','11-settings','12-privacy','13-honorable-plus','14-indexing','15-empty-search','16-dark-home','17-dark-memories','18-dark-terms-ai'];
(async()=>{const root=__dirname,browser=await chromium.launch({headless:true});const page=await browser.newPage({viewport:{width:432,height:936},deviceScaleFactor:1});for(const id of ids){await page.goto(`file://${path.join(root,'render.html')}?screen=${id}`);await page.waitForTimeout(250);await page.screenshot({path:path.join(root,`${id}.png`)});}await browser.close();})();
