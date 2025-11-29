// Единая точка доступа к API
function getAuthToken() {
    return localStorage.getItem('token');
}

function checkAuth() {
    if (!getAuthToken()) {
        window.location.href = '/login.html';
    }
}

async function apiRequest(url, options = {}) {
    const token = getAuthToken();
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }

    const config = {
        ...options,
        headers
    };

    // console.log(url);
    // console.log(config);
    const res = await fetch(url, config);
    if (!res.ok) {
        let errorText = `HTTP ${res.status}`;
        try {
            const error = await res.json();
            errorText = error.message || errorText;
        } catch (e) {
            // ignore
        }
        throw new Error(errorText);
    }
    return res.json();
}

// Публичный API
const api = {
    login: (login, password) => apiRequest('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ login, password })
    }),
    getWizard: () => apiRequest('/wizards/me'),
    getMySpells: (page = 0, size = 20) => apiRequest(`/my-spellbook?page=${page}&size=${size}`),
    getAllSpells: (page = 0, size = 100) => apiRequest(`/all-spellbook?page=${page}&size=${size}`),
    getArtifacts: (page = 0, size = 20) => apiRequest(`/artifacts?page=${page}&size=${size}`),
    getActiveSpells: () => apiRequest('/temlates/spells/active/mine'),
    getAllVictims: () => apiRequest('/victims'),
    castSpell: (spellId, victimId) => apiRequest('/temlates/spells/cast', {
        method: 'POST',
        body: JSON.stringify({ spellId, victimId })
    })
};

