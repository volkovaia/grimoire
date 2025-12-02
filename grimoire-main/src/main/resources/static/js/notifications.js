// js/notifications.js

let stompClient = null;

function connectToWebSockets() {
    const token = localStorage.getItem('token');

    if (!token) {
        console.warn("Нет токена, WebSocket не будет подключен.");
        return;
    }

    // 1. Создаем подключение
    // Важно: передаем токен в параметре запроса, так как Interceptor на бэке ждет ?token=...
    const socket = new SockJS('/ws?token=' + encodeURIComponent(token));
    stompClient = Stomp.over(socket);

    // Отключаем лишний дебаг в консоли (по желанию)
    // stompClient.debug = null;

    // 2. Коннектимся
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        // 3. Подписываемся на личную очередь
        // На бэке вы шлете в "/user/{id}/queue/notifications"
        // На фронте Spring подменяет "/user" на вашу сессию, поэтому подписываемся так:
        stompClient.subscribe('/user/queue/notifications', function (messageOutput) {
            handleNotification(JSON.parse(messageOutput.body));
        });

    }, function (error) {
        console.error('WebSocket error:', error);
        // Здесь можно реализовать логику реконнекта через 5 секунд
        setTimeout(connectToWebSockets, 5000);
    });
}

function handleNotification(notification) {
    console.log("Получено уведомление:", notification);

    // notification.type -> например "ARTIFACT_AWARDED"
    // notification.message -> текст сообщения

    showToast(notification.type, notification.message);
}

function showToast(type, message) {
    const container = document.getElementById('notification-container');
    if (!container) return;

    // Создаем элемент
    const toast = document.createElement('div');
    toast.className = `toast ${type}`; // Добавляем класс типа для цвета
    toast.textContent = message;

    // Добавляем в DOM
    container.appendChild(toast);

    // Анимация появления (небольшая задержка чтобы CSS transition сработал)
    requestAnimationFrame(() => {
        toast.classList.add('show');
    });

    // Удаляем через 5 секунд
    setTimeout(() => {
        toast.classList.remove('show');
        // Ждем окончания анимации исчезновения перед удалением из DOM
        setTimeout(() => {
            container.removeChild(toast);
        }, 500);
    }, 5000);
}

// Запускаем подключение при загрузке страницы
document.addEventListener('DOMContentLoaded', connectToWebSockets);
