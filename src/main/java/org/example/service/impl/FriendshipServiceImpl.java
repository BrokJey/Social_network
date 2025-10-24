package org.example.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.FriendshipDTO;
import org.example.dto.UserDTO;
import org.example.entity.Friendship;
import org.example.entity.User;
import org.example.entity.enums.FriendshipStatus;
import org.example.mapper.FriendshipMapper;
import org.example.mapper.UserMapper;
import org.example.service.FriendshipService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipServiceImpl implements FriendshipService {

    @PersistenceContext
    private final EntityManager entityManager;

    private final FriendshipMapper friendshipMapper;

    private final UserMapper userMapper;

    @Override
    public FriendshipDTO sendFriendRequest(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            log.error("Error: пользователь не может отправить запрос самому себе");
            throw new IllegalArgumentException();
        }

        User requester = entityManager.find(User.class, requesterId);
        User receiver = entityManager.find(User.class, receiverId);

        if (requester == null || receiver == null) {
            log.error("Error: отправитель или получатель не найдены (requesterId = {}, receiverId = {}", requesterId, receiverId);
            throw new IllegalArgumentException("Один из пользователей не найден");
        }

        // Проверка на существующий запрос или дружбу
        List<Friendship> existing = entityManager.createQuery("SELECT f FROM Friendship f WHERE (f.requester.id = :r1 AND f.receiver.id = :r2) OR (f.requester.id = :r2 AND f.receiver.id = :r1)", Friendship.class)
                .setParameter("r1", requesterId)
                .setParameter("r2", receiverId)
                .getResultList();

        if (!existing.isEmpty()) {
            log.error("Error: отношение уже существует между пользователями {} и {}", requesterId, receiverId);
            throw new IllegalArgumentException("Запрос уже существует или пользователи уже друзья");
        }

        Friendship friendship = Friendship.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendshipStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        entityManager.persist(friendship);
        log.info("Info: запрос в друзья отправлен от {} к {}", requesterId, receiverId);
        return friendshipMapper.toDTO(friendship);
    }

    @Override
    public void acceptFriendRequest(Long requestId) {
        Friendship friendship = entityManager.find(Friendship.class, requestId);

        if (friendship == null) {
            log.error("Error: запрос в друзья c id = {} не найден", requestId);
            throw new IllegalArgumentException("Запрос не найден");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            log.error("Error: запрос в друзья уже обработан (id = {}, status = {})", requestId, friendship.getStatus());
            throw new IllegalStateException("Запрос уже обработан");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        entityManager.merge(friendship);

        log.info("Info: запрос в друзья принят id = {}", requestId);
    }

    @Override
    public void declineFriendRequest(Long requestId) {
        Friendship friendship = entityManager.find(Friendship.class, requestId);

        if (friendship == null) {
            log.error("Error: запрос в друзья с id = {} не найден", requestId);
            throw new IllegalArgumentException("Запрос не найден");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            log.error("Error: запрос в друзья уже обработан (id = {}, status = {})", requestId, friendship.getStatus());
            throw new IllegalArgumentException("Запрос уже обработан");
        }

        friendship.setStatus(FriendshipStatus.DECLINED);
        entityManager.merge(friendship);

        log.info("Info: запрос в друзья отклонен id = {}", requestId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        List<Friendship> friendships = entityManager.createQuery("SELECT f FROM Friendship f WHERE (f.requester.id = :userId AND f.receiver.id = :friendId) OR (f.requester.id = :friendId AND f.receiver.id = :userId)", Friendship.class)
                .setParameter("userId", userId)
                .setParameter("friendId", friendId)
                .getResultList();

        if (friendships.isEmpty()) {
            log.error("Error: дружба между пользователями {} и {} не найдена", userId, friendId);
            throw new IllegalArgumentException("Дружба не найдена");
        }

        //Удалить все на случай дубликатов
        for (Friendship friendship : friendships) {
            entityManager.remove(friendship);
        }

        log.info("Info: дружба между пользователями {} и {} удалена", userId, friendId);
    }

    @Override
    public List<UserDTO> getFriends(Long userId) {
        // Получаем друзей, где пользователь является отправителем запроса
        List<User> friendsAsRequester = entityManager.createQuery(
                "SELECT f.receiver FROM Friendship f " +
                "WHERE f.requester.id = :userId AND f.status = :status", User.class)
                .setParameter("userId", userId)
                .setParameter("status", FriendshipStatus.ACCEPTED)
                .getResultList();

        // Получаем друзей, где пользователь является получателем запроса
        List<User> friendsAsReceiver = entityManager.createQuery(
                "SELECT f.requester FROM Friendship f " +
                "WHERE f.receiver.id = :userId AND f.status = :status", User.class)
                .setParameter("userId", userId)
                .setParameter("status", FriendshipStatus.ACCEPTED)
                .getResultList();

        // Объединяем списки
        List<User> allFriends = new ArrayList<>();
        allFriends.addAll(friendsAsRequester);
        allFriends.addAll(friendsAsReceiver);

        log.info("Info: найдено {} друзей для пользователя {}", allFriends.size(), userId);

        return allFriends.stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    public List<FriendshipDTO> getPendingRequests(Long userId) {
        List<Friendship> pendingRequests = entityManager.createQuery("SELECT f FROM Friendship f " +
                "WHERE f.receiver.id = :userId AND f.status = :status", Friendship.class)
                .setParameter("userId", userId)
                .setParameter("status", FriendshipStatus.PENDING)
                .getResultList();

        log.info("Info: найдено {} входящих запросов в друзья для пользователя {}", pendingRequests.size(), userId);

        return pendingRequests.stream()
                .map(friendshipMapper::toDTO)
                .toList();
    }
}
