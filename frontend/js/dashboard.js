// Глобальные переменные
let currentUser = null;
let posts = [];
let friends = [];
let communities = [];

// Инициализация дашборда
document.addEventListener('DOMContentLoaded', async function() {
    // Проверяем авторизацию
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'index.html';
        return;
    }

    try {
        // Загружаем данные пользователя
        await loadUserData();
        await loadInitialData();
        
        // Настраиваем обработчики событий
        setupEventListeners();
        
    } catch (error) {
        console.error('Ошибка инициализации:', error);
        showAlert('Ошибка загрузки данных', 'danger');
    }
});

// Загрузка данных пользователя
async function loadUserData() {
    try {
        currentUser = await apiClient.getMyProfile();
        document.getElementById('userName').textContent = currentUser.username;
    } catch (error) {
        console.error('Ошибка загрузки профиля:', error);
        throw error;
    }
}

// Загрузка начальных данных
async function loadInitialData() {
    try {
        // Загружаем посты, друзей и сообщества параллельно
        const [postsData, friendsData, communitiesData] = await Promise.all([
            apiClient.getAllPosts(),
            apiClient.getMyFriends(),
            apiClient.getMyCommunities()
        ]);

        posts = postsData;
        friends = friendsData;
        communities = communitiesData;

        // Обновляем статистику
        updateStatistics();
        
        // Показываем ленту новостей
        showFeed();
        
    } catch (error) {
        console.error('Ошибка загрузки данных:', error);
        throw error;
    }
}

// Настройка обработчиков событий
function setupEventListeners() {
    // Обработчик создания поста
    document.getElementById('createPostForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        await createPost();
    });
}

// Создание поста
async function createPost() {
    const content = document.getElementById('postContent').value.trim();
    
    if (!content) {
        showAlert('Введите текст поста', 'warning');
        return;
    }

    try {
        const newPost = await apiClient.createPost(content);
        posts.unshift(newPost);
        
        // Очищаем форму
        document.getElementById('postContent').value = '';
        
        // Обновляем отображение
        showFeed();
        updateStatistics();
        
        showAlert('Пост успешно создан!', 'success');
        
    } catch (error) {
        showAlert(`Ошибка создания поста: ${error.message}`, 'danger');
    }
}

// Показать ленту новостей
function showFeed() {
    const container = document.getElementById('postsContainer');
    
    if (posts.length === 0) {
        container.innerHTML = `
            <div class="dashboard-card">
                <div class="card-body text-center">
                    <i class="fas fa-newspaper fa-3x text-muted mb-3"></i>
                    <h5>Пока нет постов</h5>
                    <p class="text-muted">Создайте первый пост или добавьте друзей!</p>
                </div>
            </div>
        `;
        return;
    }

    const postsHtml = posts.map(post => createPostHtml(post)).join('');
    container.innerHTML = postsHtml;
}

// Создание HTML для поста
function createPostHtml(post) {
    const isMyPost = currentUser && post.authorId === currentUser.id;
    
    return `
        <div class="post-card">
            <div class="post-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <strong>${post.authorUsername || 'Неизвестный пользователь'}</strong>
                        <small class="text-muted ms-2">${formatDate(post.createAt)}</small>
                    </div>
                    ${isMyPost ? `
                        <div class="dropdown">
                            <button class="btn btn-sm btn-outline-secondary" data-bs-toggle="dropdown">
                                <i class="fas fa-ellipsis-v"></i>
                            </button>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="#" onclick="editPost(${post.id})">Редактировать</a></li>
                                <li><a class="dropdown-item text-danger" href="#" onclick="deletePost(${post.id})">Удалить</a></li>
                            </ul>
                        </div>
                    ` : ''}
                </div>
            </div>
            <div class="post-content">
                <p>${escapeHtml(post.content)}</p>
            </div>
            <div class="post-actions">
                <button class="btn btn-sm btn-outline-primary" onclick="addComment(${post.id})">
                    <i class="fas fa-comment"></i> Комментировать
                </button>
                <button class="btn btn-sm btn-outline-success" onclick="likePost(${post.id})">
                    <i class="fas fa-heart"></i> Нравится
                </button>
            </div>
        </div>
    `;
}

// Показать мои посты
function showMyPosts() {
    const myPosts = posts.filter(post => post.authorId === currentUser.id);
    
    if (myPosts.length === 0) {
        document.getElementById('postsContainer').innerHTML = `
            <div class="dashboard-card">
                <div class="card-body text-center">
                    <i class="fas fa-edit fa-3x text-muted mb-3"></i>
                    <h5>У вас пока нет постов</h5>
                    <p class="text-muted">Создайте свой первый пост!</p>
                </div>
            </div>
        `;
        return;
    }

    const postsHtml = myPosts.map(post => createPostHtml(post)).join('');
    document.getElementById('postsContainer').innerHTML = postsHtml;
}

// Показать друзей
function showFriends() {
    const container = document.getElementById('postsContainer');
    
    if (friends.length === 0) {
        container.innerHTML = `
            <div class="dashboard-card">
                <div class="card-body text-center">
                    <i class="fas fa-users fa-3x text-muted mb-3"></i>
                    <h5>У вас пока нет друзей</h5>
                    <p class="text-muted">Найдите друзей и отправьте им запросы!</p>
                    <button class="btn btn-primary" onclick="showSearchUsers()">Найти друзей</button>
                </div>
            </div>
        `;
        return;
    }

    const friendsHtml = friends.map(friend => `
        <div class="friend-card">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <strong>${friend.username}</strong>
                    <br>
                    <small class="text-muted">${friend.firstName} ${friend.lastName}</small>
                </div>
                <div>
                    <button class="btn btn-sm btn-outline-primary" onclick="sendMessage(${friend.id})">
                        <i class="fas fa-comment"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="removeFriend(${friend.id})">
                        <i class="fas fa-user-times"></i>
                    </button>
                </div>
            </div>
        </div>
    `).join('');

    container.innerHTML = `
        <div class="dashboard-card">
            <div class="card-header">
                <h5><i class="fas fa-users"></i> Мои друзья (${friends.length})</h5>
            </div>
            <div class="card-body">
                ${friendsHtml}
            </div>
        </div>
    `;
}

// Показать сообщества
function showCommunities() {
    const container = document.getElementById('postsContainer');
    
    if (communities.length === 0) {
        container.innerHTML = `
            <div class="dashboard-card">
                <div class="card-body text-center">
                    <i class="fas fa-building fa-3x text-muted mb-3"></i>
                    <h5>Вы не состоите в сообществах</h5>
                    <p class="text-muted">Создайте сообщество или присоединитесь к существующему!</p>
                    <button class="btn btn-primary" onclick="showCreateCommunity()">Создать сообщество</button>
                </div>
            </div>
        `;
        return;
    }

    const communitiesHtml = communities.map(community => `
        <div class="friend-card">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <strong>${community.name}</strong>
                    <br>
                    <small class="text-muted">${community.description}</small>
                </div>
                <div>
                    <button class="btn btn-sm btn-outline-danger" onclick="leaveCommunity(${community.id})">
                        <i class="fas fa-sign-out-alt"></i> Покинуть
                    </button>
                </div>
            </div>
        </div>
    `).join('');

    container.innerHTML = `
        <div class="dashboard-card">
            <div class="card-header">
                <h5><i class="fas fa-building"></i> Мои сообщества (${communities.length})</h5>
            </div>
            <div class="card-body">
                ${communitiesHtml}
            </div>
        </div>
    `;
}

// Показать сообщения
function showMessages() {
    document.getElementById('postsContainer').innerHTML = `
        <div class="dashboard-card">
            <div class="card-body text-center">
                <i class="fas fa-comments fa-3x text-muted mb-3"></i>
                <h5>Сообщения</h5>
                <p class="text-muted">Функция сообщений будет добавлена в следующей версии</p>
            </div>
        </div>
    `;
}

// Обновление статистики
function updateStatistics() {
    document.getElementById('postsCount').textContent = posts.filter(post => post.authorId === currentUser.id).length;
    document.getElementById('friendsCount').textContent = friends.length;
    document.getElementById('communitiesCount').textContent = communities.length;
}

// Показать модальное окно создания сообщества
function showCreateCommunity() {
    const modal = new bootstrap.Modal(document.getElementById('createCommunityModal'));
    modal.show();
}

// Создать сообщество
async function createCommunity() {
    const name = document.getElementById('communityName').value.trim();
    const description = document.getElementById('communityDescription').value.trim();
    
    if (!name) {
        showAlert('Введите название сообщества', 'warning');
        return;
    }

    try {
        const newCommunity = await apiClient.createCommunity(name, description);
        communities.push(newCommunity);
        
        // Закрываем модальное окно
        const modal = bootstrap.Modal.getInstance(document.getElementById('createCommunityModal'));
        modal.hide();
        
        // Очищаем форму
        document.getElementById('communityName').value = '';
        document.getElementById('communityDescription').value = '';
        
        // Обновляем статистику
        updateStatistics();
        
        showAlert('Сообщество успешно создано!', 'success');
        
    } catch (error) {
        showAlert(`Ошибка создания сообщества: ${error.message}`, 'danger');
    }
}

// Показать поиск пользователей
function showSearchUsers() {
    document.getElementById('postsContainer').innerHTML = `
        <div class="dashboard-card">
            <div class="card-header">
                <h5><i class="fas fa-search"></i> Поиск друзей</h5>
            </div>
            <div class="card-body">
                <div class="mb-3">
                    <input type="text" class="form-control" id="searchUsername" placeholder="Введите имя пользователя">
                </div>
                <button class="btn btn-primary" onclick="searchUsers()">Найти</button>
                <div id="searchResults" class="mt-3"></div>
            </div>
        </div>
    `;
}

// Поиск пользователей
async function searchUsers() {
    const username = document.getElementById('searchUsername').value.trim();
    
    if (!username) {
        showAlert('Введите имя пользователя для поиска', 'warning');
        return;
    }

    try {
        const users = await apiClient.searchUsers({ firstName: username });
        const resultsContainer = document.getElementById('searchResults');
        
        if (users.length === 0) {
            resultsContainer.innerHTML = '<p class="text-muted">Пользователи не найдены</p>';
            return;
        }

        const usersHtml = users.map(user => `
            <div class="friend-card">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <strong>${user.username}</strong>
                        <br>
                        <small class="text-muted">${user.firstName} ${user.lastName}</small>
                    </div>
                    <button class="btn btn-sm btn-primary" onclick="sendFriendRequest(${user.id})">
                        <i class="fas fa-user-plus"></i> Добавить в друзья
                    </button>
                </div>
            </div>
        `).join('');

        resultsContainer.innerHTML = usersHtml;
        
    } catch (error) {
        showAlert(`Ошибка поиска: ${error.message}`, 'danger');
    }
}

// Отправить запрос в друзья
async function sendFriendRequest(userId) {
    try {
        await apiClient.sendFriendRequest(userId);
        showAlert('Запрос в друзья отправлен!', 'success');
    } catch (error) {
        showAlert(`Ошибка отправки запроса: ${error.message}`, 'danger');
    }
}

// Удалить друга
async function removeFriend(friendId) {
    if (!confirm('Вы уверены, что хотите удалить этого друга?')) {
        return;
    }

    try {
        await apiClient.removeFriend(friendId);
        friends = friends.filter(friend => friend.id !== friendId);
        showFriends();
        updateStatistics();
        showAlert('Друг удален', 'success');
    } catch (error) {
        showAlert(`Ошибка удаления друга: ${error.message}`, 'danger');
    }
}

// Покинуть сообщество
async function leaveCommunity(communityId) {
    if (!confirm('Вы уверены, что хотите покинуть это сообщество?')) {
        return;
    }

    try {
        await apiClient.leaveCommunity(communityId);
        communities = communities.filter(community => community.id !== communityId);
        showCommunities();
        updateStatistics();
        showAlert('Вы покинули сообщество', 'success');
    } catch (error) {
        showAlert(`Ошибка выхода из сообщества: ${error.message}`, 'danger');
    }
}

// Удалить пост
async function deletePost(postId) {
    if (!confirm('Вы уверены, что хотите удалить этот пост?')) {
        return;
    }

    try {
        await apiClient.deletePost(postId);
        posts = posts.filter(post => post.id !== postId);
        showFeed();
        updateStatistics();
        showAlert('Пост удален', 'success');
    } catch (error) {
        showAlert(`Ошибка удаления поста: ${error.message}`, 'danger');
    }
}

// Выход из системы
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'index.html';
}

// Вспомогательные функции
function formatDate(dateString) {
    if (!dateString) return 'Неизвестно';
    const date = new Date(dateString);
    return date.toLocaleString('ru-RU');
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function showAlert(message, type = 'danger') {
    // Создаем уведомление
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show position-fixed" 
             style="top: 20px; right: 20px; z-index: 9999;" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    document.body.insertAdjacentHTML('beforeend', alertHtml);
    
    // Автоматически скрываем через 5 секунд
    setTimeout(() => {
        const alert = document.querySelector('.alert');
        if (alert) {
            alert.remove();
        }
    }, 5000);
}


