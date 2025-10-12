# Social Network REST API

RESTful веб-приложение социальной сети на Spring Boot с JWT аутентификацией.

## Технический стек

- **Java 17**
- **Spring Boot 3.4.4**
- **PostgreSQL** (порт 5555)
- **JPA/Hibernate**
- **JWT Security**
- **Liquibase** для миграций БД
- **Maven** для сборки
- **JUnit 5 + Mockito** для тестирования

## Предварительные требования

1. **Java 17** или выше
2. **PostgreSQL** установлен и запущен
3. **Maven** установлен

## Установка и запуск

### 1. Настройка базы данных

1. Убедитесь, что PostgreSQL запущен на порту **5555**
2. Создайте базу данных:
```sql
CREATE DATABASE social_network;
```

### 2. Сборка проекта

```bash
mvn clean compile
```

### 3. Запуск приложения

```bash
mvn spring-boot:run
```

Или через IDE запустите класс `org.example.Main`

Приложение запустится на порту **8080**

## API Endpoints

### Аутентификация
- `POST /auth/register` - Регистрация пользователя
- `POST /auth/login` - Вход в систему

### Пользователи
- `GET /users/{id}` - Получить пользователя по ID
- `PUT /users/{id}` - Обновить информацию пользователя
- `GET /users/search` - Поиск пользователей (firstName, lastName, age, gender)

### Посты
- `POST /posts/user/{userId}` - Создать пост от пользователя
- `POST /posts/community/{communityId}` - Создать пост в сообществе
- `GET /posts/{id}` - Получить пост по ID
- `GET /posts/user/{userId}` - Получить посты пользователя
- `GET /posts/community/{communityId}` - Получить посты сообщества
- `GET /posts/all` - Получить все посты
- `PUT /posts/{id}` - Обновить пост
- `DELETE /posts/{id}` - Удалить пост

### Комментарии
- `POST /comment` - Добавить комментарий
- `GET /comment/post/{postId}` - Получить комментарии к посту
- `DELETE /comment/{id}?postId={postId}` - Удалить комментарий

### Сообщества
- `POST /community/create/{adminId}` - Создать сообщество
- `GET /community/all` - Получить все сообщества
- `GET /community/show/{userId}` - Получить сообщества пользователя
- `POST /community/join` - Присоединиться к сообществу
- `POST /community/leave` - Покинуть сообщество
- `GET /community/members/{communityId}` - Получить участников сообщества
- `DELETE /community/delete/{id}?adminId={adminId}` - Удалить сообщество

### Друзья
- `POST /friendships/request/{sender}/{receiver}` - Отправить запрос в друзья
- `POST /friendships/accept/{id}` - Принять запрос в друзья
- `DELETE /friendships/decline/{id}` - Отклонить запрос в друзья
- `GET /friendships/get/{id}` - Получить список друзей
- `GET /friendships/pending/{id}` - Получить входящие запросы
- `DELETE /friendships/remove?userId={userId}&friendId={friendId}` - Удалить друга

### Чаты и сообщения
- `POST /chat/create/{user1}/{user2}` - Создать приватный чат
- `POST /chat/group` - Создать групповой чат
- `GET /chat/user/{id}` - Получить чаты пользователя
- `DELETE /chat/{id}?requesterId={requesterId}` - Удалить чат
- `POST /messages/send/{chatId}/{senderId}` - Отправить сообщение
- `GET /messages/{id}` - Получить сообщение по ID
- `GET /messages/between/{userId1}/{userId2}` - Получить сообщения между пользователями
- `GET /messages/user/{userId}` - Получить все сообщения пользователя
- `DELETE /messages/{id}` - Удалить сообщение

### Роли
- `POST /roles` - Создать роль
- `GET /roles/{id}` - Получить роль по ID
- `GET /roles/search?name={name}` - Найти роль по имени
- `GET /roles/all` - Получить все роли
- `POST /roles/assign?userId={userId}&roleId={roleId}` - Назначить роль пользователю
- `POST /roles/remove?userId={userId}&roleId={roleId}` - Удалить роль у пользователя
- `DELETE /roles/{id}` - Удалить роль

## Тестирование с Postman

### 1. Регистрация пользователя
```json
POST http://localhost:8080/auth/register
Content-Type: application/json

{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com"
}
```

### 2. Вход в систему
```json
POST http://localhost:8080/auth/login
Content-Type: application/json

{
    "username": "testuser",
    "password": "password123"
}
```

Ответ содержит JWT токен:
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 3. Использование токена
Для всех остальных запросов добавьте заголовок:
```
Authorization: Bearer {your-jwt-token}
```

### 4. Создание поста
```json
POST http://localhost:8080/posts/user/1
Authorization: Bearer {your-jwt-token}
Content-Type: application/json

{
    "content": "Мой первый пост!"
}
```

## Запуск тестов

```bash
mvn test
```

## Структура проекта

```
src/
├── main/
│   ├── java/org/example/
│   │   ├── config/          # Конфигурация Hibernate
│   │   ├── controller/      # REST контроллеры
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA сущности
│   │   ├── mapper/         # MapStruct мапперы
│   │   ├── repository/     # Репозитории
│   │   ├── security/       # JWT безопасность
│   │   ├── service/        # Бизнес-логика
│   │   └── Main.java       # Точка входа
│   └── resources/
│       ├── application.properties
│       └── db/changelog/   # Liquibase миграции
└── test/                   # Unit тесты
```

## Возможные проблемы

1. **Ошибка подключения к БД**: Убедитесь, что PostgreSQL запущен на порту 5555
2. **Порт 8080 занят**: Измените `server.port` в application.properties
3. **Ошибки миграций**: Проверьте, что база данных `social_network` создана

## Логирование

Приложение использует SLF4J с логированием:
- **INFO** - успешные операции
- **ERROR** - ошибки обработки
- **WARN** - предупреждения

Логи выводятся в консоль и могут быть настроены через `logback-spring.xml`
