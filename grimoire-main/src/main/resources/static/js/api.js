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

// ... [Неизмененный код apiRequest, api, getAuthToken, checkAuth и т.д.] ...

// --- УДАЛИТЬ ЭТОТ НЕИСПОЛЬЗУЕМЫЙ КОНФИГ ---
// const API_CAST_URL = '/api/cast-spell';
// ------------------------------------------

/**
 * Асинхронно получает список Артефактов (Заклинаний) через аутентифицированный API
 * и заполняет элемент <select>.
 *
 * @param {string} selectId - ID элемента <select>, который нужно заполнить.
 */
async function loadArtifactsAndPopulateSelect(selectId) { // Переименовано для ясности
    const spellSelect = document.getElementById(selectId);

    if (!spellSelect) {
        console.error(`Элемент <select> с ID "${selectId}" не найден.`);
        return;
    }

    try {
        // --- ИСПОЛЬЗУЕМ API-МЕТОД, КОТОРЫЙ УЖЕ ДОБАВЛЯЕТ АВТОРИЗАЦИЮ ---
        //const responseData = await api.getArtifacts(0, 100);
        // Если вам нужны ВСЕ заклинания (а не только артефакты), используйте:
        const responseData = await api.getAllSpells(0, 100);
        // -----------------------------------------------------------------

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
            // Успешный запрос, но пустой или неверный массив
            throw new Error(`Список заклинаний пуст или имеет некорректный формат.`);
        }

        // 1.1. Заполнение <select>
        spellSelect.innerHTML = '<option value="" disabled selected>-- Выберите заклинание --</option>';
        spells.forEach(spell => {
            const option = document.createElement('option');
            // Используйте то поле, которое является именем заклинания в вашей модели Artifact
            option.value = spell.id;
            option.textContent = spell.name || spell.artifactName || `Заклинание #${spell.id}`;
            spellSelect.appendChild(option);
        });

    } catch (error) {
        console.error("Ошибка при загрузке списка заклинаний:", error);
        // Выводим сообщение об ошибке, перехваченное из apiRequest
        spellSelect.innerHTML = `<option value="">Ошибка загрузки: ${error.message || 'Неизвестно'}</option>`;
    }
}


/**
 * Обрабатывает отправку формы для наложения заклинания.
 */
async function handleCastSpellSubmit(event) {
    event.preventDefault();
    checkAuth(); // Проверяем авторизацию перед отправкой

    const casterId = document.getElementById('casterId').value;
    const targetId = document.getElementById('targetId').value;
    const spellSelect = document.getElementById('spellSelect');
    const spellId = spellSelect.value;
    const messageArea = document.getElementById('messageArea');

    messageArea.textContent = 'Наложение заклинания...';
    messageArea.style.color = 'orange';

    if (!spellId || !targetId) { // casterId обычно берется из сессии, не из поля
        messageArea.textContent = "Пожалуйста, выберите заклинание и укажите ID Цели.";
        messageArea.style.color = 'red';
        return;
    }

    try {
        // 2.2. Отправка POST-запроса на бэкенд через ваш API-метод
        // Обратите внимание: ваш метод castSpell принимает spellId и victimId, а не targetId.
        const result = await api.castSpell(parseInt(spellId), parseInt(targetId));

        messageArea.textContent = `Заклинание "${spellSelect.options[spellSelect.selectedIndex].text}" успешно наложено на цель ${targetId}!`;
        messageArea.style.color = 'green';
        // Дополнительная логика (обновление состояния и т.д.)

    } catch (error) {
        console.error("Ошибка наложения заклинания:", error);
        messageArea.textContent = `Ошибка наложения заклинания: ${error.message || 'Произошла ошибка'}`;
        messageArea.style.color = 'red';
    }
}