const BASE_URL = 'http://localhost:8080/api';

const AUTH_KEYS = ['token', 'role', 'email', 'userId'];

function getAuthValue(key) {
    return sessionStorage.getItem(key) || localStorage.getItem(key);
}

function getToken() { return getAuthValue('token'); }
function getRole()  { return getAuthValue('role'); }
function getEmail() { return getAuthValue('email'); }
function getUserId(){ return getAuthValue('userId'); }

let toastHost = null;

function authHeaders() {
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + getToken()
    };
}

function logout() {
    AUTH_KEYS.forEach(key => {
        sessionStorage.removeItem(key);
        localStorage.removeItem(key);
    });
    window.location.href = '/index.html';
}

function clearAuthOnly() {
    AUTH_KEYS.forEach(key => {
        sessionStorage.removeItem(key);
        localStorage.removeItem(key);
    });
}

function decodeJwtPayload(token) {
    try {
        const parts = token.split('.');
        if (parts.length < 2) return null;
        const payload = parts[1]
            .replace(/-/g, '+')
            .replace(/_/g, '/');
        const json = atob(payload);
        return JSON.parse(json);
    } catch (_) {
        return null;
    }
}

function isTokenExpired(token) {
    const payload = decodeJwtPayload(token);
    if (!payload || !payload.exp) return false;
    const nowSec = Math.floor(Date.now() / 1000);
    return nowSec >= payload.exp;
}

let authFailureHandled = false;

function handleAuthFailure() {
    if (authFailureHandled) return;
    authFailureHandled = true;
    clearAuthOnly();
    window.location.href = '/index.html?expired=1';
}

async function parseJsonSafe(res) {
    const text = await res.text();
    if (!text) return {};
    try {
        return JSON.parse(text);
    } catch (_) {
        return { success: false, message: text };
    }
}

async function apiPost(endpoint, body, useAuth = true) {
    const headers = useAuth ? authHeaders() : { 'Content-Type': 'application/json' };
    const res = await fetch(BASE_URL + endpoint, {
        method: 'POST',
        headers,
        body: JSON.stringify(body)
    });
    if (useAuth && (res.status === 401 || res.status === 403)) {
        handleAuthFailure();
        return { success: false, message: 'Session expired. Please login again.' };
    }
    return parseJsonSafe(res);
}

async function apiGet(endpoint) {
    const res = await fetch(BASE_URL + endpoint, {
        method: 'GET',
        headers: authHeaders()
    });
    if (res.status === 401 || res.status === 403) {
        handleAuthFailure();
        return { success: false, message: 'Session expired. Please login again.' };
    }
    return parseJsonSafe(res);
}

async function apiPut(endpoint, body) {
    const res = await fetch(BASE_URL + endpoint, {
        method: 'PUT',
        headers: authHeaders(),
        body: JSON.stringify(body)
    });
    if (res.status === 401 || res.status === 403) {
        handleAuthFailure();
        return { success: false, message: 'Session expired. Please login again.' };
    }
    return parseJsonSafe(res);
}

async function apiDelete(endpoint) {
    const res = await fetch(BASE_URL + endpoint, {
        method: 'DELETE',
        headers: authHeaders()
    });
    if (res.status === 401 || res.status === 403) {
        handleAuthFailure();
        return { success: false, message: 'Session expired. Please login again.' };
    }
    return parseJsonSafe(res);
}

function showAlert(id, message, type) {
    const el = document.getElementById(id);
    if (!el) return;
    el.textContent = message;
    el.className = 'alert alert-' + type;
    showToast(message, type === 'danger' ? 'danger' : 'success');
    setTimeout(() => { el.className = 'alert'; }, 4000);
}

function getToastHost() {
    if (toastHost && document.body.contains(toastHost)) return toastHost;
    toastHost = document.getElementById('toastStack');
    if (!toastHost) {
        toastHost = document.createElement('div');
        toastHost.id = 'toastStack';
        toastHost.className = 'toast-stack';
        document.body.appendChild(toastHost);
    }
    return toastHost;
}

function showToast(message, type = 'info', timeout = 1400) {
    if (!message) return;
    const host = getToastHost();
    const toast = document.createElement('div');
    toast.className = 'toast toast-' + type;
    toast.textContent = message;
    host.appendChild(toast);
    setTimeout(() => {
        toast.remove();
    }, timeout);
}

function registerButtonPopups() {
    if (window.__buttonPopupBound) return;
    window.__buttonPopupBound = true;

    document.addEventListener('click', event => {
        const target = event.target.closest('button, .btn, input[type="button"], input[type="submit"]');
        if (!target || target.disabled) return;

        if (target.id === 'toastStack') return;

        let label = (target.textContent || target.value || '').trim();
        if (!label) label = 'Action';
        if (label.length > 28) label = label.slice(0, 28) + '...';

        showToast(label + ' clicked', 'info', 900);
    }, true);
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', registerButtonPopups);
} else {
    registerButtonPopups();
}

function requireAuth(expectedRole) {
    const token = getToken();
    const role  = getRole();
    if (!token) { window.location.href = '/index.html'; return; }
    if (isTokenExpired(token)) {
        handleAuthFailure();
        return;
    }
    if (expectedRole && role !== expectedRole) {
        window.location.href = '/index.html';
    }
}
