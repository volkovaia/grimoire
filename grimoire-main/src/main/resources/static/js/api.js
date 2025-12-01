// Единая точка доступа к API
function getAuthToken() {
    return localStorage.getItem('token');
}

function saveAuthToken(token) {
    localStorage.setItem('token', token); // <-- Используем тот же ключ, что и в getAuthToken()
}

function checkAuth() {
    if (!getAuthToken()) {
        window.location.href = '/login.html';
    }
}

async function apiRequest(url, options = {}) {
    // Если API_BASE_URL определен, используем его
    const fullUrl = (typeof API_BASE_URL !== 'undefined' ? API_BASE_URL : '') + url;

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

    const res = await fetch(fullUrl, config);
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
    }).then(response => {
              // Предполагается, что ответ: { token: "..." }
              if (response.token) {
                  saveAuthToken(response.token); // СОХРАНЕНИЕ ТОКЕНА
              }
              return response;
          }),
    getWizard: () => apiRequest('/wizards/me'),
    getMySpells: (page = 0, size = 20) => apiRequest(`/my-spellbook?page=${page}&size=${size}`),
    getAllSpells: (page = 0, size = 100) => apiRequest(`/all-spellbook?page=${page}&size=${size}`),
    getArtifacts: (page = 0, size = 20) => apiRequest(`/artifacts?page=${page}&size=${size}`),
    getActiveSpells: () => apiRequest('/temlates/spells/active/mine'),
    getOthersActiveSpells: () => apiRequest('/temlates/spells/active/others'),
    getAllVictims: () => apiRequest('/victims'),
    // НОВЫЙ МЕТОД ДЛЯ ПОЛУЧЕНИЯ ГИЛЬДИЙ
    getGuildsByLevel: (level) => apiRequest(`/guilds?level=${level}`),
    castSpell: (spellId, victimId) => apiRequest('/temlates/spells/cast', {
        method: 'POST',
        body: JSON.stringify({ spellId, victimId })
    })
};

// --- ФУНКЦИИ, КОТОРЫЕ МОГУТ БЫТЬ В api.js, ЕСЛИ ОНИ ОБЩИЕ ---
// Оставлю здесь, чтобы не терять ваш код, но они могут быть перенесены в dashboard.html

/**
 * Асинхронно получает список Артефактов (Заклинаний) через аутентифицированный API
 * и заполняет элемент <select>.
 */
async function loadArtifactsAndPopulateSelect(selectId) { // Переименовано для ясности
    const spellSelect = document.getElementById(selectId);

    if (!spellSelect) {
        console.error(`Элемент <select> с ID "${selectId}" не найден.`);
        return;
    }

    try {
        const responseData = await api.getAllSpells(0, 100);

        let spells = [];

        // Адаптация для Spring Data REST (Page-объект)
        if (responseData._embedded && responseData._embedded.artifacts) {
            spells = responseData._embedded.artifacts;
        } else if (responseData.content) {
             spells = responseData.content;
        } else if (Array.isArray(responseData)) {
            spells = responseData;
        }


        if (!Array.isArray(spells) || spells.length === 0) {
            throw new Error(`Список заклинаний пуст или имеет некорректный формат.`);
        }

        // 1.1. Заполнение <select>
        spellSelect.innerHTML = '<option value="" disabled selected>-- Выберите заклинание --</option>';
        spells.forEach(spell => {
            const option = document.createElement('option');
            option.value = spell.id;
            option.textContent = spell.name || spell.artifactName || `Заклинание #${spell.id}`;
            spellSelect.appendChild(option);
        });

    } catch (error) {
        console.error("Ошибка при загрузке списка заклинаний:", error);
        spellSelect.innerHTML = `<option value="">Ошибка загрузки: ${error.message || 'Неизвестно'}</option>`;
    }
}


async function loadSpellsForCasting(selectId) {
    const selectElement = document.getElementById(selectId);
    selectElement.innerHTML = '<option value="">Загрузка заклинаний...</option>';

    if (!getAuthToken()) {
        selectElement.innerHTML = '<option value="">Необходимо авторизоваться</option>';
        return;
    }

    try {
        const data = await api.getMySpells(0, 100);

        selectElement.innerHTML = '<option value="">-- Выберите заклинание --</option>';

        if (data.content && data.content.length > 0) {
            data.content.forEach(spell => {
                const option = document.createElement('option');
                option.value = spell.id;
                option.textContent = `[${spell.id}] ${spell.name} (Требуемый уровень: ${spell.requiredGuildLevel})`;
                selectElement.appendChild(option);
            });
        } else {
            selectElement.innerHTML = '<option value="">Нет доступных заклинаний</option>';
        }

    } catch (error) {
        console.error('Ошибка загрузки доступных заклинаний:', error);
        selectElement.innerHTML = `<option value="">Ошибка загрузки: ${error.message || 'Произошла ошибка'}</option>`;
    }
}

async function handleCastSpellSubmit(event) {
    event.preventDefault();

    const targetId = document.getElementById('targetId').value;
    const spellSelect = document.getElementById('spellSelect');
    const spellId = spellSelect.value;
    const messageArea = document.getElementById('messageArea');

    messageArea.textContent = 'Наложение заклинания...';
    messageArea.style.color = 'orange';

    if (!spellId || !targetId) {
        messageArea.textContent = "Пожалуйста, выберите заклинание и укажите ID Цели.";
        messageArea.style.color = 'red';
        return;
    }

    try {
        const result = await api.castSpell(parseInt(spellId), parseInt(targetId));

        const selectedSpellName = spellSelect.options[spellSelect.selectedIndex].textContent;

        messageArea.textContent = `Заклинание "${result.spellName || selectedSpellName}" (ID: ${result.castId}) успешно наложено на цель ${result.victimId || targetId}! Статус: ${result.status}`;
        messageArea.style.color = 'green';

    } catch (error) {
        console.error("Ошибка наложения заклинания:", error);
        messageArea.textContent = `Ошибка наложения заклинания: ${error.message || 'Произошла ошибка'}`;
        messageArea.style.color = 'red';
    }
}

// ⚠️ Предполагается, что api.js подключается перед login.html