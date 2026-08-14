const id = new URLSearchParams(location.search).get('screen') || '01-home';
const phone = document.querySelector('#phone');
const darkChrome = new Set([
  '03-search-focus', '04-ai-searching', '07-media-viewer', '12-privacy',
  '13-honorable-plus', '16-dark-home', '17-dark-memories', '18-dark-terms-ai'
]);
phone.classList.toggle('dark', darkChrome.has(id));
phone.dataset.screen = id;

const A = 'assets/';

const masthead = (section = 'Private Intelligence', folio = 'VOL. 01<br>ON DEVICE') => `
  <header class="masthead">
    <div class="wordmark"><span class="monogram">H</span> HONORABLE</div>
    <div class="folio">${section}<br>${folio}</div>
  </header>`;

const nav = active => `
  <nav class="rail" aria-label="Primary">
    ${[
      ['⌂', 'Home'], ['▦', 'Memories'], ['§', 'Terms'], ['◷', 'Activity'], ['☷', 'Settings']
    ].map(([icon, label]) => `<span class="${label === active ? 'active' : ''}"><i>${icon}</i><small>${label}</small></span>`).join('')}
  </nav>`;

const search = (text = 'Describe a memory…', label = 'Search your library', cls = '') => `
  <div class="glass-search ${cls}">
    <div class="search-copy"><small>${label}</small><b>${text}</b></div>
    <div class="submit">↗</div>
  </div>`;

const memory = (img, title, detail, number = '01', time = '') => `
  <article class="memory">
    <img src="${A + img}" alt="">
    <span class="media-no">${number}</span>
    ${time ? `<span class="timecode">${time}</span>` : ''}
    <div class="cap"><b>${title}</b><small>${detail}</small></div>
  </article>`;

const sheet = () => `
  <div class="contact-sheet">
    ${memory('memory_tennis_4k.webp', 'Saturday match', 'outdoors · blue shirt', '01')}
    ${memory('memory_snow_car_4k.webp', 'Winter drive', 'snow · red vehicle', '02')}
    ${memory('memory_birthday_4k.webp', 'Birthday light', 'cake · candles', '03')}
  </div>`;

const privacyLedger = (dark = false) => `
  <div class="privacy-ledger ${dark ? 'ink' : ''}">
    <div class="seal">H</div>
    <div><b>Private by architecture</b><br><span>Media, index, and queries remain here.</span></div>
    <small>LOCAL / 01</small>
  </div>`;

const home = (dark = false) => `
  <section class="screen home ${dark ? 'ink' : ''}">
    ${masthead('Blueglass Intelligence', 'EDITION 01<br>MMXXVI')}
    <div class="blueglass-hero">
      <p class="eyebrow">PRIVATE INTELLIGENCE / EST. 2026</p>
      <div class="hero-number">01</div>
      <h1 class="display">Find what<br><em>matters.</em></h1>
      <div class="hero-rule"></div>
      <p class="dek">Describe a moment in your own words. Honorable searches the private library on this device.</p>
      ${search('A white beach with tall grass', 'Ask your memories', 'ice')}
    </div>
    <div class="feature-ledger">
      <div class="ledger-item"><span class="index-no">01 / MEMORY INDEX</span><h3>Memories</h3><p>Find any moment →</p></div>
      <div class="ledger-item"><span class="index-no">02 / CLARITY</span><h3>Terms AI</h3><p>Read the fine print →</p></div>
    </div>
    ${privacyLedger(dark)}
    ${nav('Home')}
  </section>`;

const memories = (dark = false) => `
  <section class="screen ${dark ? 'ink' : ''}">
    ${masthead('Memory Intelligence', '9,812 ITEMS<br>PRIVATE INDEX')}
    <div class="edition-header">
      <p class="eyebrow">THE PERSONAL MEMORY INDEX / 01</p>
      <h1 class="display">Memories.</h1>
      <p class="dek">No filenames. No folders. Describe the scene as you remember it.</p>
    </div>
    ${search('Describe a moment…', 'Natural-language search', dark ? 'dark-field' : '')}
    <div class="ticker">
      <span class="tag active">Suggested</span><span class="tag">White beach</span><span class="tag">Red car</span><span class="tag">Birthday light</span>
    </div>
    <div class="section-head"><h2>Recently surfaced</h2><small>CONTACT SHEET 01</small></div>
    ${sheet()}
    ${nav('Memories')}
  </section>`;

const searchFocus = () => `
  <section class="screen no-rail ink focus-screen">
    <header class="masthead"><div class="wordmark"><span class="monogram">H</span> MEMORIES AI</div><div class="close-box">×</div></header>
    <p class="eyebrow" style="margin-top:22px">QUERY / NATURAL LANGUAGE / LOCAL</p>
    <h1 class="display">Describe<br><span class="outline">the</span><br><em>moment.</em></h1>
    <p class="dek" style="margin-top:20px">A scene, color, person, sign—even a feeling.</p>
    ${search('blue shirt at tennis', 'Your description', 'dark-field')}
    <div class="primary-action focus-submit"><b>SEARCH PRIVATE LIBRARY</b><span>↗</span></div>
    <div class="local-note">◈ Private query · nothing leaves this device</div>
  </section>`;

const searching = () => `
  <section class="screen no-rail ink searching-screen">
    <div class="search-mark">H</div>
    <p class="eyebrow">LOCAL SEMANTIC SEARCH / ACTIVE</p>
    <h1 class="display">Searching<br>your <em>library.</em></h1>
    <div class="query-caption">“tennis outside”</div>
    <p class="dek" style="margin-top:22px;text-align:center">Comparing scenes, text, color, time, and visual meaning.</p>
  </section>`;

const resultScreen = (video = false) => `
  <section class="screen results-screen">
    ${masthead('Memory Search', video ? 'VIDEOS / 04<br>BEST FIRST' : 'ALL MEDIA / 12<br>BEST FIRST')}
    <div class="result-topline"><div><p class="eyebrow">RESULTS FOR</p><b>${video ? 'tennis highlights' : 'tennis outside'}</b></div><span class="close-box">↙</span></div>
    <div class="ticker"><span class="tag ${video ? '' : 'active'}">All</span><span class="tag">Photos</span><span class="tag ${video ? 'active' : ''}">Videos</span><span class="tag">Date</span><span class="tag">Place</span></div>
    <article class="best-result">
      <img src="${A}memory_tennis_4k.webp" alt="Best matching tennis memory">
      <span class="match-index">01 / Strongest match</span>
      ${video ? '<span class="duration">01:42</span>' : ''}
      <div class="result-copy">
        <p class="eyebrow">${video ? 'MATCHING VIDEO MOMENT' : 'MATCHING PHOTOGRAPH'}</p>
        <h1 class="result-title">Blue shirt.<br>Open court.</h1>
        <div class="result-reason"><span>Visual scene · outdoors · tennis</span><b class="confidence">High confidence</b></div>
      </div>
    </article>
    <div class="section-head"><h2>More matches</h2><small>VIEW CONTACT SHEET →</small></div>
    <div class="more-results">
      ${memory('memory_snow_car_4k.webp', 'Winter drive', 'possible match', '02', video ? '00:36' : '')}
      ${memory('memory_birthday_4k.webp', 'Birthday light', 'possible match', '03', video ? '02:18' : '')}
    </div>
    ${nav('Memories')}
  </section>`;

const viewer = () => `
  <section class="screen full-bleed no-rail viewer-screen">
    <img class="viewer-image" src="${A}memory_tennis_4k.webp" alt="Tennis memory">
    <div class="viewer-toolbar"><span>←</span><span>↗</span></div>
    <div class="viewer-caption">
      <p class="eyebrow">BEST LOCAL MATCH / 01</p>
      <h1 class="display">The<br>Saturday<br><em>match.</em></h1>
      <div class="viewer-metadata">
        <div><small>Confidence</small><b>HIGH</b></div>
        <div><small>Moment</small><b>01:42</b></div>
        <div><small>Evidence</small><b>VISUAL</b></div>
      </div>
    </div>
  </section>`;

const terms = (dark = false) => `
  <section class="screen ${dark ? 'ink' : ''}">
    ${masthead('Terms Intelligence', 'PRIVATE REVIEW<br>NOT LEGAL ADVICE')}
    <div class="terms-cover">
      <p class="eyebrow">AGREEMENT INTELLIGENCE / 02</p>
      <h1 class="display">See beneath<br>the <em>fine print.</em></h1>
      <p class="dek">Turn dense language into a clear, private decision.</p>
      <div class="import-grid"><div><b>↗</b><small>Paste link</small></div><div><b>¶</b><small>Paste text</small></div><div><b>↑</b><small>Import file</small></div></div>
    </div>
    <div class="primary-action"><b>ANALYZE AGREEMENT</b><span>↗</span></div>
    ${privacyLedger(dark)}
    ${nav('Terms')}
  </section>`;

const termsResult = () => `
  <section class="screen">
    ${masthead('Terms Intelligence', 'ANALYSIS 01<br>COMPLETE')}
    <div class="edition-header"><p class="eyebrow">AGREEMENT HEALTH</p><h1 class="display">The verdict.</h1></div>
    <div class="risk-hero"><div class="risk-ring"><span class="metric-number">58</span></div><div class="risk-copy"><p class="eyebrow">MODERATE / REVIEW</p><h2>A few clauses need attention.</h2><p>Renewal and dispute limits deserve a closer look.</p></div></div>
    <div class="summary-slab"><p class="eyebrow">THE ONE-MINUTE READ</p><p class="quote">Renews annually. Cancellation is possible before billing; refunds are limited.</p></div>
    <div class="disclosures">${['Important', 'Watch out', 'Good', 'Money', 'Cancellation', 'Privacy', 'Your rights'].map((x, i) => `<div class="disclosure"><span class="num">0${i + 1}</span><b>${x}</b><span>⌄</span></div>`).join('')}</div>
  </section>`;

const activity = () => `
  <section class="screen">
    ${masthead('Intelligence Ledger', 'ACTIVITY<br>AUG 13')}
    <div class="edition-header"><p class="eyebrow">A QUIET RECORD / LOCAL</p><h1 class="display">Activity.</h1><p class="dek">What Honorable understood, in chronological order.</p></div>
    <div class="timeline">
      ${[
        ['TODAY', 'Tennis outside', '12 private matches found'],
        ['TODAY', 'Subscription terms', 'Moderate agreement health'],
        ['12 AUG', 'Library synchronized', '48 new memories indexed'],
        ['11 AUG', 'Flight screenshot', 'Air Canada text matched']
      ].map((x, i) => `<div class="timeline-row"><div class="date">${x[0]}</div><div class="event"><span class="index-no" style="margin:0 0 8px">0${i + 1}</span><b>${x[1]}</b><p>${x[2]}</p></div><div class="arrow">›</div></div>`).join('')}
    </div>
    ${nav('Activity')}
  </section>`;

const settingGroup = (label, items) => `
  <div class="setting-group"><div class="setting-label">${label}</div>${items.map((x, i) => `<div class="setting-row"><span class="setting-icon">${String(i + 1).padStart(2, '0')}</span><div><b>${x[0]}</b><small>${x[1]}</small></div><span>›</span></div>`).join('')}</div>`;

const settings = () => `
  <section class="screen">
    ${masthead('Personal Edition', 'SETTINGS<br>VERSION 0.1')}
    <div class="edition-header"><p class="eyebrow">SHAPE THE EXPERIENCE</p><h1 class="display">Settings.</h1></div>
    <div class="upgrade-slab"><p class="eyebrow" style="color:var(--deep-navy)">HONORABLE PLUS / ANNUAL</p><h2>Elevate the private library.</h2><p>Advanced intelligence, same private foundation. →</p></div>
    ${settingGroup('Privacy', [['Privacy promise', 'Everything stays local'], ['Permissions', 'Photos and videos']])}
    ${settingGroup('Intelligence', [['AI & search', 'On-device processing'], ['Storage & index', '9,812 local memories']])}
    ${settingGroup('Honorable', [['Appearance', 'Blueglass Editorial'], ['About', 'Version 0.1.0']])}
    ${nav('Settings')}
  </section>`;

const privacy = () => `
  <section class="screen no-rail ink">
    <header class="masthead"><div class="wordmark"><span class="monogram">H</span> THE PROMISE</div><div class="close-box">×</div></header>
    <div class="promise-mark">H</div>
    <p class="eyebrow" style="text-align:center">PRIVATE BY ARCHITECTURE / ALWAYS</p>
    <h1 class="display privacy-title">Your memories<br><em>stay yours.</em></h1>
    <p class="dek privacy-dek">Intelligence should not require a cloud copy of your life.</p>
    <div class="node-grid">
      <div class="privacy-node"><span class="index-no">01</span><b>Local AI</b></div>
      <div class="privacy-node"><span class="index-no">02</span><b>Local OCR</b></div>
      <div class="privacy-node"><span class="index-no">03</span><b>Local index</b></div>
      <div class="privacy-node"><span class="index-no">04</span><b>No upload</b></div>
    </div>
    <div class="local-note">◈ Photos, frames, and search text remain here</div>
  </section>`;

const plus = () => `
  <section class="screen no-rail plus-screen">
    <header class="masthead"><div class="wordmark"><span class="monogram">H</span> PRIVATE EDITION</div><div class="close-box">×</div></header>
    <p class="eyebrow" style="margin-top:27px">HONORABLE / PLUS / 2026</p>
    <h1 class="display">More ways<br>to <em>understand.</em></h1>
    <p class="dek" style="margin-top:15px">Advanced intelligence. The same private foundation.</p>
    <div class="benefit-list">${['Advanced Memories AI', 'Advanced video search', 'Terms AI', 'No ads', 'Future intelligence'].map((x, i) => `<div class="benefit"><span>0${i + 1}</span>${x}</div>`).join('')}</div>
    <div class="plans"><div class="plan"><small>Monthly</small><div class="plan-price">$5.99</div></div><div class="plan selected"><small>Annual · Selected</small><div class="plan-price">$39.99</div></div></div>
    <div class="primary-action"><b>CONTINUE WITH ANNUAL</b><span>↗</span></div>
    <div class="local-note">Preview only · store processing not configured</div>
  </section>`;

const indexing = () => `
  <section class="screen no-rail indexing-screen">
    ${masthead('Blueglass Intelligence', 'INDEX BUILD<br>IN PROGRESS')}
    <p class="eyebrow" style="margin-top:56px">LOCAL INDEX / SAFE TO LEAVE</p>
    <h1 class="display">Preparing<br>your <em>library.</em></h1>
    <div class="progress-number">76%</div>
    <div class="progress-track"><span></span></div>
    <div class="progress-meta"><span>7,482 / 9,812</span><span>Photos complete</span></div>
    <p class="dek" style="margin-top:31px">Representative video frames are next. Processing remains on this device.</p>
    ${privacyLedger(false)}
  </section>`;

const empty = () => `
  <section class="screen no-rail empty-screen">
    ${masthead('Memory Search', 'RESULTS / 00<br>LOW CONFIDENCE')}
    <div class="empty-zero">0</div>
    <p class="eyebrow">NO CLEAR MATCH / YET</p>
    <h1 class="display">Try another<br><em>detail.</em></h1>
    <p class="dek">Add a place, color, date, or words visible in the image.</p>
    <div class="empty-suggestions"><span class="tag">Add a date</span><span class="tag">Choose media</span><span class="tag">Add color</span></div>
    <div class="primary-action" style="margin-top:auto"><b>REFINE THE SEARCH</b><span>↗</span></div>
  </section>`;

const screens = {
  '01-home': () => home(false),
  '02-memories': () => memories(false),
  '03-search-focus': searchFocus,
  '04-ai-searching': searching,
  '05-search-results': () => resultScreen(false),
  '06-video-results': () => resultScreen(true),
  '07-media-viewer': viewer,
  '08-terms-ai': () => terms(false),
  '09-terms-result': termsResult,
  '10-activity': activity,
  '11-settings': settings,
  '12-privacy': privacy,
  '13-honorable-plus': plus,
  '14-indexing': indexing,
  '15-empty-search': empty,
  '16-dark-home': () => home(true),
  '17-dark-memories': () => memories(true),
  '18-dark-terms-ai': () => terms(true)
};

document.querySelector('#screen').innerHTML = (screens[id] || screens['01-home'])();
