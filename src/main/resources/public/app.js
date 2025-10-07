// Simple auth/token utilities
const APP_TOKEN_KEY = 'auth_token';
const APP_ROLE_KEY = 'auth_role';

export function saveAuth(token, role){ localStorage.setItem(APP_TOKEN_KEY, token); localStorage.setItem(APP_ROLE_KEY, role||''); }
export function getToken(){ return localStorage.getItem(APP_TOKEN_KEY); }
export function getRole(){ return localStorage.getItem(APP_ROLE_KEY)||''; }
export function clearAuth(){ localStorage.removeItem(APP_TOKEN_KEY); localStorage.removeItem(APP_ROLE_KEY); }

export async function api(path, opts={}){
  const headers = Object.assign({'Content-Type':'application/json','Accept':'application/json'}, opts.headers||{});
  const t = getToken(); if (t) headers['Authorization'] = 'Bearer ' + t;
  const res = await fetch(path, Object.assign({}, opts, { headers }));
  const ct = res.headers.get('content-type') || '';
  let payload;
  if (ct.includes('application/json')) {
    payload = await res.json().catch(()=>({ success:false, error:'Invalid JSON' }));
  } else {
    const text = await res.text();
    payload = { success: res.ok, error: text || res.statusText };
  }
  if (!res.ok || payload.success === false) throw new Error(payload.error || res.statusText);
  return payload;
}

export function requireAuth(){ if(!getToken()) window.location.href = '/'; }


