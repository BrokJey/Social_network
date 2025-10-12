# Руководство по тестированию API социальной сети

## Предварительные требования

1. **PostgreSQL** должен быть запущен на порту **5555**
2. **База данных** `social_network` должна быть создана
3. **Приложение** должно быть запущено на порту **8080**

## Быстрый старт

### 1. Создание базы данных
```sql
CREATE DATABASE social_network;
```

### 2. Запуск приложения
```bash
mvn spring-boot:run
```

### 3. Импорт коллекции Postman
Импортируйте файл `postman_collection.json` в Postman

## Последовательность тестирования

### Шаг 1: Регистрация пользователя
```http
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com"
}
```

### Шаг 2: Вход в систему
```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

**Сохраните JWT токен из ответа!**

### Шаг 3: Обновление профиля
```http
PUT http://localhost:8080/users/1
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
  "firstName": "Иван",
  "lastName": "Петров",
  "age": 25,
  "gender": "MALE"
}
```

### Шаг 4: Создание поста
```http
POST http://localhost:8080/posts/user/1
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
  "content": "Мой первый пост в социальной сети!"
}
```

### Шаг 5: Регистрация второго пользователя
```http
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "username": "testuser2",
  "password": "password123",
  "email": "test2@example.com"
}
```

### Шаг 6: Отправка запроса в друзья
```http
POST http://localhost:8080/friendships/request/1/2
Authorization: Bearer YOUR_JWT_TOKEN
```

### Шаг 7: Создание сообщества
```http
POST http://localhost:8080/community/create/1?name=Программисты&description=Сообщество для программистов
Authorization: Bearer YOUR_JWT_TOKEN
```

### Шаг 8: Создание чата
```http
POST http://localhost:8080/chat/create/1/2
Authorization: Bearer YOUR_JWT_TOKEN
```

### Шаг 9: Отправка сообщения
```http
POST http://localhost:8080/messages/send/1/1
Content-Type: application/json
Authorization: Bearer YOUR_JWT_TOKEN

{
  "content": "Привет! Как дела?"
}
```

## Проверка функциональности

### Поиск пользователей
```http
GET http://localhost:8080/users/search?firstName=Иван
Authorization: Bearer YOUR_JWT_TOKEN
```

### Получение всех постов
```http
GET http://localhost:8080/posts/all
Authorization: Bearer YOUR_JWT_TOKEN
```

### Получение друзей
```http
GET http://localhost:8080/friendships/get/1
Authorization: Bearer YOUR_JWT_TOKEN
```

### Получение сообщений между пользователями
```http
GET http://localhost:8080/messages/between/1/2
Authorization: Bearer YOUR_JWT_TOKEN
```

## Возможные проблемы

1. **Ошибка подключения к БД**: Убедитесь, что PostgreSQL запущен на порту 5555
2. **Ошибка 401**: Проверьте правильность JWT токена
3. **Ошибка 404**: Убедитесь, что пользователь с указанным ID существует
4. **Ошибка 500**: Проверьте логи приложения

## Логи

Логи приложения можно найти в консоли или в файле логов (если настроен).

## Структура базы данных

Приложение автоматически создаст все необходимые таблицы при первом запуске благодаря Liquibase миграциям.
