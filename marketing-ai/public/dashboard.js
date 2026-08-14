let state;
const escapeHtml=value=>String(value??'').replace(/[&<>'"]/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c]));
const post=(url,data={})=>fetch(url,{method:'POST',headers:{'content-type':'application/json'},body:JSON.stringify(data)}).then(async r=>{const x=await r.json();if(!r.ok)throw new Error(x.error||'Request failed');return x});
async function load(){state=await fetch('/api/dashboard').then(r=>r.json());render();}
function render(){
  const high=state.opportunities.filter(x=>x.intent==='HIGH INTENT').length;
  const values=[state.analytics.visitors,state.analytics.signups,`${state.analytics.conversionRate}%`,high];
  document.querySelectorAll('#metrics b').forEach((node,i)=>node.textContent=values[i]);
  document.querySelector('#connectors').innerHTML=state.connectors.map(x=>`<div class="connector"><b>${escapeHtml(x.platform)}</b><em>${escapeHtml(x.status)}</em></div>`).join('');
  document.querySelector('#trends').innerHTML=state.trends.map(x=>`<div class="trend"><b>${escapeHtml(x.painPoint)}</b><span>${x.share}%</span></div>`).join('');
  document.querySelector('#opportunities').innerHTML=state.opportunities.map(x=>`<article class="opportunity"><div class="score">${x.score}</div><div><h3>${escapeHtml(x.topic)}</h3><p>${escapeHtml(x.angle)}</p><div class="tags"><span class="tag">${escapeHtml(x.platform)}</span><span class="tag">${escapeHtml(x.intent)}</span><span class="tag">${escapeHtml(x.painPoint)}</span><span class="tag">${x.engagement} ENGAGEMENT</span></div><a class="source-link mock" aria-disabled="true">MOCK SOURCE · NO PUBLIC LINK</a></div><small>${new Date(x.publishedAt).toLocaleDateString()}</small></article>`).join('');
  document.querySelector('#audiences').innerHTML=state.audiences.map(x=>`<div class="draft"><h3>${escapeHtml(x.segment)}</h3><span>${escapeHtml(x.signal)}</span></div>`).join('');
  document.querySelector('#alternatives').innerHTML=state.alternatives.map(x=>`<div class="draft"><h3>${escapeHtml(x.name)}</h3><span>${escapeHtml(x.insight)}</span></div>`).join('');
  document.querySelector('#campaign-list').innerHTML=state.campaigns.length?state.campaigns.map(x=>`<div class="draft"><div class="tags"><span class="tag">VARIANT ${escapeHtml(x.variant)}</span><span class="tag">${escapeHtml(x.status)}</span></div><h3>${escapeHtml(x.name)}</h3><span>${escapeHtml(x.message)}</span></div>`).join(''):'<p class="empty">No experiments yet.</p>';
  document.querySelector('#drafts').innerHTML=state.drafts.length?state.drafts.slice().reverse().map(x=>`<article class="draft"><div class="tags"><span class="tag">${escapeHtml(x.format)}</span><span class="tag">${escapeHtml(x.status)}</span></div><h3>${escapeHtml(x.hook)}</h3><p>${x.beats.map(escapeHtml).join(' → ')}</p><strong>${escapeHtml(x.cta)}</strong>${x.status==='DRAFT'?`<br><button data-approve="${x.id}">Approve for manual publishing</button>`:''}</article>`).join(''):'<p class="empty">No drafts yet. Generate one from the strongest mock opportunity.</p>';
  const bars=items=>items.length?items.map(x=>`<div class="trend"><b>${escapeHtml(x.label)}</b><span>${x.count}</span></div>`).join(''):'<p class="empty">No attributed signups yet.</p>';
  document.querySelector('#sources').innerHTML=bars(state.analytics.bySource);document.querySelector('#campaigns').innerHTML=bars(state.analytics.byCampaign);
  document.querySelectorAll('[data-approve]').forEach(button=>button.onclick=async()=>{await post(`/api/content/drafts/${button.dataset.approve}/approve`);await load();});
}
document.querySelector('#generate').onclick=async()=>{const button=document.querySelector('#generate');button.disabled=true;try{await post('/api/content/drafts',{format:document.querySelector('#format').value,opportunityId:state.opportunities[0]?.id});await load();}finally{button.disabled=false;}};
document.querySelector('#create-experiment').onclick=async()=>{const button=document.querySelector('#create-experiment');button.disabled=true;try{await post('/api/campaigns',{name:'Memory retrieval launch',variant:'A',message:'Find any memory.'});await post('/api/campaigns',{name:'Memory retrieval launch',variant:'B',message:'Stop scrolling through 20,000 photos.'});await load();}finally{button.disabled=false;}};
load().catch(error=>{document.querySelector('#opportunities').innerHTML=`<p class="empty">${escapeHtml(error.message)}</p>`;});
