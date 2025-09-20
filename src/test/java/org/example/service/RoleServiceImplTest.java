package org.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.example.dto.RoleDTO;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.entity.enums.RoleType;
import org.example.mapper.RoleMapper;
import org.example.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class RoleServiceImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRole_success() {
        RoleDTO inputDto = RoleDTO.builder().name(RoleType.ROLE_USER).build();
        Role roleEntity = new Role();
        roleEntity.setName(RoleType.ROLE_USER);
        Role savedRole = new Role();
        savedRole.setId(1L);
        savedRole.setName(RoleType.ROLE_USER);

        RoleDTO expectedDto = RoleDTO.builder().id(1L).name(RoleType.ROLE_USER).build();

        when(roleMapper.fromDTO(inputDto)).thenReturn(roleEntity);
        doAnswer(invocation -> {
            Role r = invocation.getArgument(0);
            r.setId(1L);
            return null;
        }).when(entityManager).persist(roleEntity);

        when(roleMapper.toDTO(any(Role.class))).thenReturn(expectedDto);

        RoleDTO result = roleService.createRole(inputDto);

        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getName(), result.getName());
        verify(entityManager).persist(roleEntity);
        verify(roleMapper).toDTO(roleEntity);
    }

    @Test
    void createRole_nullName_throwsException() {
        RoleDTO inputDTO = RoleDTO.builder().name(null).build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> roleService.createRole(inputDTO));
        assertEquals("Имя роли не может быть null", ex.getMessage());
        verifyNoInteractions(entityManager, roleMapper);
    }

    @Test
    void getRoleById_found() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleType.ROLE_USER);

        RoleDTO dto = RoleDTO.builder().id(1L).name(RoleType.ROLE_USER).build();

        when(entityManager.find(Role.class, 1L)).thenReturn(role);
        when(roleMapper.toDTO(role)).thenReturn(dto);

        RoleDTO result = roleService.getRoleById(1L).orElse(null);

        assertEquals(dto.getId(), result.getId());
        verify(entityManager).find(Role.class, 1L);
        verify(roleMapper).toDTO(role);
    }

    @Test
    void getRoleByName_success() {
        Role role = new Role();
        role.setName(RoleType.ROLE_USER);

        RoleDTO dto = RoleDTO.builder().name(RoleType.ROLE_USER).build();

        TypedQuery<Role> mockQuery = mock(TypedQuery.class);

        when(entityManager.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("name"), "ROLE_USER")).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(List.of(role));
        when(roleMapper.toDTO(role)).thenReturn(dto);

        RoleDTO result = roleService.getRoleByName("ROLE_USER").orElse(null);

        assertEquals(dto.getName(), result.getName());
        verify(entityManager).createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class);
        verify(mockQuery).setParameter("name", "ROLE_USER");
        verify(mockQuery).getResultList();
        verify(roleMapper).toDTO(role);

    }

    @Test
    void deleteRole_existingRole_deletesSuccessfully() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleType.ROLE_USER);

        when(entityManager.find(Role.class, 1L)).thenReturn(role);

        roleService.deleteRole(1L);

        verify(entityManager).find(Role.class, 1L);
        verify(entityManager).remove(role);
    }

    @Test
    void deleteRole_nonExisting_doesNothing() {
        when(entityManager.find(Role.class, 99L)).thenReturn(null);

        roleService.deleteRole(99L);

        verify(entityManager).find(Role.class, 99L);
        verify(entityManager, never()).remove(any());
    }

    @Test
    void getAllRoles_success() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName(RoleType.ROLE_USER);

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName(RoleType.ROLE_ADMIN);

        List<Role> roles = List.of(role1, role2);

        TypedQuery<Role> query = mock(TypedQuery.class);
        when(entityManager.createQuery("SELECT r FROM Role r", Role.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(roles);
        when(roleMapper.toDTO(role1)).thenReturn(RoleDTO.builder().id(1L).name(RoleType.ROLE_USER).build());
        when(roleMapper.toDTO(role2)).thenReturn(RoleDTO.builder().id(2L).name(RoleType.ROLE_ADMIN).build());

        List<RoleDTO> result = roleService.getAllRoles();

        assertEquals(2, result.size());
        verify(entityManager).createQuery("SELECT r FROM Role r", Role.class);
        verify(query).getResultList();
        verify(roleMapper).toDTO(role1);
        verify(roleMapper).toDTO(role2);
    }

    @Test
    void assignRoleToUser_success() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleType.ROLE_USER);

        User user = new User();
        user.setId(10L);

        when(entityManager.find(User.class, 10L)).thenReturn(user);
        when(entityManager.find(Role.class, 1L)).thenReturn(role);
        when(roleMapper.toDTO(role)).thenReturn(RoleDTO.builder().id(1L).name(RoleType.ROLE_USER).build());

        Optional<RoleDTO> result = roleService.assignRoleToUser(10L, 1L);

        assertTrue(result.isPresent());
        assertEquals(RoleType.ROLE_USER, result.get().getName());
        assertTrue(user.getRoles().contains(role));
        verify(entityManager).merge(user);
    }

    @Test
    void assignRoleToUser_userNotFound_returnsEmpty() {
        when(entityManager.find(User.class, 10L)).thenReturn(null);

        Optional<RoleDTO> result = roleService.assignRoleToUser(10L, 1L);

        assertTrue(result.isEmpty());
        verify(entityManager, never()).merge(any());
    }

    @Test
    void removeRoleFromUser_success() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleType.ROLE_USER);

        User user = new User();
        user.setId(10L);
        user.getRoles().add(role);

        when(entityManager.find(User.class, 10L)).thenReturn(user);
        when(entityManager.find(Role.class, 1L)).thenReturn(role);
        when(roleMapper.toDTO(role)).thenReturn(RoleDTO.builder().id(1L).name(RoleType.ROLE_USER).build());

        Optional<RoleDTO> result = roleService.removeRoleFromUser(10L, 1L);

        assertTrue(result.isPresent());
        assertFalse(user.getRoles().contains(role));
        verify(entityManager).merge(user);
    }

    @Test
    void removeRoleFromUser_roleNotFound_returnsEmpty() {
        User user = new User();
        user.setId(10L);

        when(entityManager.find(User.class, 10L)).thenReturn(user);
        when(entityManager.find(Role.class, 1L)).thenReturn(null);

        Optional<RoleDTO> result = roleService.removeRoleFromUser(10L, 1L);

        assertTrue(result.isEmpty());
        verify(entityManager, never()).merge(any());
    }
}
