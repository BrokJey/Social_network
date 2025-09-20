package org.example.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.RoleDTO;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.mapper.RoleMapper;
import org.example.service.RoleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {
    private final EntityManager entityManager;
    private final RoleMapper roleMapper;

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        if (roleDTO.getName() == null) {
            log.error("Error: имя роли не может быть null");
            throw new IllegalArgumentException("Имя роли не может быть null");
        }

        Role role = roleMapper.fromDTO(roleDTO);
        entityManager.persist(role);
        log.info("Info: Роль создана: id {}, name {}", role.getId(), role.getName());
        return roleMapper.toDTO(role);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = entityManager.find(Role.class, id);
        if (role != null) {
            log.info("Info: Роль удалена: id {}, name {}", role.getId(), role.getName());
            entityManager.remove(role);
        }
        else {
            log.error("Error: роли нет");
        }
    }

    @Override
    public Optional<RoleDTO> getRoleById(Long id) {
        Role role = entityManager.find(Role.class, id);
        if (role != null) {
            log.info("Info: Роль найдена с id {}", role.getId());
            return Optional.of(roleMapper.toDTO(role));
        }
        log.error("Error: Роль не найдена с id {}", id);
        return Optional.empty();
    }

    @Override
    public Optional<RoleDTO> getRoleByName(String name) {
        TypedQuery<Role> query = entityManager.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class);
        query.setParameter("name", name);
        List<Role> roles = query.getResultList();
        if (roles.isEmpty()) {
            log.error("Error: Роли не найдены с именем: {}", name);
            return Optional.empty();
        }
        log.info("Info: Роли найденные с именем: {}", name);
        return Optional.of(roleMapper.toDTO(roles.get(0)));
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = entityManager.createQuery("SELECT r FROM Role r", Role.class).getResultList();

        List<RoleDTO> result = roles.stream()
                .map(roleMapper::toDTO)
                .toList();

        log.info("Info: найдено ролей: {}", result.size());
        return result;
    }

    @Override
    public Optional<RoleDTO> assignRoleToUser(Long userId, Long roleId) {
        User user = entityManager.find(User.class, userId);
        Role role = entityManager.find(Role.class, roleId);

        if (user == null || role == null) {
            log.error("Error: пользователь {} или роль {} пусты", user != null ? user.getId() : null, role != null ? role.getId() : null);
            return Optional.empty();
        }

        user.getRoles().add(role);
        entityManager.merge(user);

        log.info("Info: пользователю {} добавлена роль {}", user.getId(), role.getId());
        return Optional.of(roleMapper.toDTO(role));
    }

    @Override
    public Optional<RoleDTO> removeRoleFromUser(Long userId, Long roleId) {
        User user = entityManager.find(User.class, userId);
        Role role = entityManager.find(Role.class, roleId);

        if (user == null || role == null) {
            log.error("Error: пользователь {} или роль {} пусты", user != null ? user.getId() : null, role != null ? role.getId() : null);
            return Optional.empty();
        }

        user.getRoles().remove(role);
        entityManager.merge(user);

        log.info("Info: пользователю {} удалили роль {}", user.getId(), role.getId());
        return Optional.of(roleMapper.toDTO(role));
    }
}
