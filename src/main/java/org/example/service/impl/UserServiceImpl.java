package org.example.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    @PersistenceContext
    private final EntityManager entityManager;

    private final UserMapper userMapper;

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        if (userDTO.getFirstName() == null) {
            log.error("Error: имя пользователя не может быть null");
            throw new IllegalArgumentException("Error: имя пользователя не может быть null");
        }
        User user = userMapper.fromDTO(userDTO);
        user.setCreatedAt(LocalDateTime.now());
        entityManager.persist(user);
        log.info("Info: пользователь зарегистрирован с id {}: {}", user.getId(), user.getFirstName());
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO updatedDTO) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            log.error("Error: пользователь не найден с id {}", id);
            throw new IllegalArgumentException("Пользователь не найден с id: " + id);
        }

        if (updatedDTO.getFirstName() != null && !updatedDTO.getFirstName().isBlank()) {
            user.setFirstName(updatedDTO.getFirstName().trim());
        }
        if (updatedDTO.getLastName() != null && !updatedDTO.getLastName().isBlank()) {
            user.setLastName(updatedDTO.getLastName().trim());
        }
        if (updatedDTO.getAge() != null) {
            user.setAge(updatedDTO.getAge());
        }

        if (updatedDTO.getGender() != null) {
            user.setGender(updatedDTO.getGender());
        }

        entityManager.merge(user);
        log.info("Info: пользователь с id {} обновлен", user.getId());
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = entityManager.find(User.class, id);
        if(user != null) {
            log.info("Info: пользователь найден с id {}", user.getId());
            return userMapper.toDTO(user);
        } else {
            log.error("Error: Пользователь не найден с id {}", id);
            throw new EntityNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public List<UserDTO> searchUsers(String firstName, String lastName, Integer age, String gender) {
        log.info("Info: Поиск пользователей по параметрам: firstName={}, lastName={}, age={}, gender={}", firstName, lastName, age, gender);
        StringBuilder sb = new StringBuilder("SELECT u FROM User u WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (firstName != null) {
            sb.append(" AND u.firstName = ?" + (params.size() + 1));
            params.add(firstName);
        }
        if (lastName != null) {
            sb.append(" AND u.lastName =?" + (params.size() + 1));
            params.add(lastName);
        }
        if (age != null) {
            sb.append(" AND u.age = ?" + (params.size() + 1));
            params.add(age);
        }
        if (gender != null) {
            sb.append(" AND u.gender = ?" + (params.size() + 1));
            params.add(gender);
        }

        TypedQuery<User> query = entityManager.createQuery(sb.toString(), User.class);
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        List<UserDTO> result = query.getResultList().stream()
                .map(userMapper::toDTO)
                .toList();

        log.info("Info: Найдено пользователей: {}", result.size());
        return result;
    }
}
