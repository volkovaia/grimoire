let stompClient = null;

function connectToWebSockets() {
    const token = localStorage.getItem('token');

    if (!token) {
        console.warn("Нет токена, WebSocket не будет подключен.");
        return;
    }

    const socket = new SockJS('/ws?token=' + encodeURIComponent(token));
    stompClient = Stomp.over(socket);


    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/user/queue/notifications', function (messageOutput) {
            handleNotification(JSON.parse(messageOutput.body));
        });

    }, function (error) {
        console.error('WebSocket error:', error);
        setTimeout(connectToWebSockets, 5000);
    });
}


function handleNotification(notification) {
    console.log("Получено уведомление:", notification);

    if (typeof window.onNotification === 'function') {
        window.onNotification(notification);
    }
    else {
        showToast(notification.type, notification.message);
    }

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

    requestAnimationFrame(() => {
        toast.classList.add('show');
    });

    // Удаляем через 5 секунд
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            container.removeChild(toast);
        }, 500);
    }, 5000);
}

// Запускаем подключение при загрузке страницы
document.addEventListener('DOMContentLoaded', connectToWebSockets);
