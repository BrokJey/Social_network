// Функции для переключения между формами
function showLogin() {
    document.getElementById('loginForm').style.display = 'block';
    document.getElementById('registerForm').style.display = 'none';
    clearAlerts();
}

function showRegister() {
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'block';
    clearAlerts();
}

// Показать уведомление
function showAlert(message, type = 'danger') {
    const alertContainer = document.getElementById('alertContainer');
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    alertContainer.innerHTML = alertHtml;
}

// Очистить уведомления
function clearAlerts() {
    document.getElementById('alertContainer').innerHTML = '';
}

// Обработка формы входа
document.getElementById('loginFormElement').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;
    
    if (!username || !password) {
        showAlert('Пожалуйста, заполните все поля');
        return;
    }
    
    try {
        const response = await apiClient.login(username, password);
        showAlert('Успешный вход!', 'success');
        
        // Сохраняем информацию о пользователе
        localStorage.setItem('user', JSON.stringify(response.user));
        
        // Перенаправляем на дашборд через 1 секунду
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 1000);
        
    } catch (error) {
        showAlert(`Ошибка входа: ${error.message}`);
    }
});

// Обработка формы регистрации
document.getElementById('registerFormElement').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const confirmPassword = document.getElementById('regConfirmPassword').value;
    
    if (!username || !email || !password || !confirmPassword) {
        showAlert('Пожалуйста, заполните все поля');
        return;
    }
    
    if (password !== confirmPassword) {
        showAlert('Пароли не совпадают');
        return;
    }
    
    if (password.length < 6) {
        showAlert('Пароль должен содержать минимум 6 символов');
        return;
    }
    
    try {
        await apiClient.register(username, email, password);
        showAlert('Регистрация успешна! Теперь вы можете войти в систему.', 'success');
        
        // Переключаемся на форму входа
        setTimeout(() => {
            showLogin();
            document.getElementById('loginUsername').value = username;
        }, 2000);
        
    } catch (error) {
        showAlert(`Ошибка регистрации: ${error.message}`);
    }
});

// Проверка авторизации при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    const token = localStorage.getItem('token');
    if (token) {
        // Если пользователь уже авторизован, перенаправляем на дашборд
        window.location.href = 'dashboard.html';
    }
});


