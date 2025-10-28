// API конфигурация
const API_BASE_URL = 'http://localhost:8080';

// Класс для работы с API
class ApiClient {
    constructor() {
        this.token = localStorage.getItem('token');
    }

    // Установить токен
    setToken(token) {
        this.token = token;
        localStorage.setItem('token', token);
    }

    // Получить заголовки
    getHeaders() {
        const headers = {
            'Content-Type': 'application/json'
        };
        
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        
        return headers;
    }

    // Базовый метод для запросов
    async request(url, options = {}) {
        const config = {
            headers: this.getHeaders(),
            ...options
        };

        try {
            const response = await fetch(`${API_BASE_URL}${url}`, config);
            
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    }

    // GET запрос
    async get(url) {
        return this.request(url, { method: 'GET' });
    }

    // POST запрос
    async post(url, data) {
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    // PUT запрос
    async put(url, data) {
        return this.request(url, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    // DELETE запрос
    async delete(url) {
        return this.request(url, { method: 'DELETE' });
    }

    // Аутентификация
    async login(username, password) {
        const response = await this.post('/auth/login', { username, password });
        this.setToken(response.token);
        return response;
    }

    async register(username, email, password) {
        return this.post('/auth/register', { username, email, password });
    }

    // Пользователи
    async getMyProfile() {
        return this.get('/users/me');
    }

    async updateMyProfile(data) {
        return this.put('/users/me', data);
    }

    async searchUsers(params) {
        const queryString = new URLSearchParams(params).toString();
        return this.get(`/users/search?${queryString}`);
    }

    // Посты
    async createPost(content) {
        return this.post('/posts/create', { content });
    }

    async getMyPosts() {
        return this.get('/posts/my-posts');
    }

    async getAllPosts() {
        return this.get('/posts/all');
    }

    async updatePost(postId, data) {
        return this.put(`/posts/${postId}`, data);
    }

    async deletePost(postId) {
        return this.delete(`/posts/${postId}`);
    }

    // Друзья
    async sendFriendRequest(receiverId) {
        return this.post(`/friendships/request/${receiverId}`);
    }

    async getMyFriends() {
        return this.get('/friendships/my-friends');
    }

    async getPendingRequests() {
        return this.get('/friendships/pending');
    }

    async acceptFriendRequest(requestId) {
        return this.post(`/friendships/accept/${requestId}`);
    }

    async declineFriendRequest(requestId) {
        return this.delete(`/friendships/decline/${requestId}`);
    }

    async removeFriend(friendId) {
        return this.delete(`/friendships/remove/${friendId}`);
    }

    // Сообщества
    async createCommunity(name, description) {
        return this.post(`/community/create?name=${encodeURIComponent(name)}&description=${encodeURIComponent(description)}`);
    }

    async getMyCommunities() {
        return this.get('/community/my-communities');
    }

    async getAllCommunities() {
        return this.get('/community/all');
    }

    async joinCommunity(communityId) {
        return this.post(`/community/join/${communityId}`);
    }

    async leaveCommunity(communityId) {
        return this.post(`/community/leave/${communityId}`);
    }

    // Комментарии
    async addComment(postId, content) {
        return this.post('/comment', { postId, content });
    }

    async getCommentsByPost(postId) {
        return this.get(`/comment/post/${postId}`);
    }

    async deleteComment(commentId, postId) {
        return this.delete(`/comment/${commentId}?postId=${postId}`);
    }

    // Чаты и сообщения
    async createChat(user2Id) {
        return this.post(`/chat/create/${user2Id}`);
    }

    async getMyChats() {
        return this.get('/chat/my-chats');
    }

    async sendMessage(chatId, content) {
        return this.post(`/messages/send/${chatId}`, { content });
    }

    async getMessagesBetweenUsers(otherUserId) {
        return this.get(`/messages/between/${otherUserId}`);
    }

    async getMyMessages() {
        return this.get('/messages/my-messages');
    }
}

// Создаем глобальный экземпляр API клиента
window.apiClient = new ApiClient();


