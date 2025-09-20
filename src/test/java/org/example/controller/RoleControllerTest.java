package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.RoleDTO;
import org.example.dto.UserDTO;
import org.example.entity.enums.RoleType;
import org.example.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createRole_success() throws Exception {
        RoleDTO input = RoleDTO.builder().name(RoleType.ROLE_USER).build();
        RoleDTO output = RoleDTO.builder().id(1L).name(RoleType.ROLE_USER).build();

        when(roleService.createRole(any(RoleDTO.class))).thenReturn(output);

        mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(RoleType.ROLE_USER));
    }

    @Test
    void getRoleById_success() throws Exception {
        RoleDTO role = RoleDTO.builder().id(1L).name(RoleType.ROLE_USER).build();
        when(roleService.getRoleById(1L)).thenReturn(Optional.of(role));

        mockMvc.perform(get("/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(RoleType.ROLE_USER));
    }

    @Test
    void deleteRole_success() throws Exception {
        RoleDTO role = RoleDTO.builder().id(1L).name(RoleType.ROLE_USER).build();
        doNothing().when(roleService).deleteRole(1L);

        mockMvc.perform(delete("/roles/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getRoleByName_success() throws Exception {
        RoleDTO role = RoleDTO.builder().id(1L).name(RoleType.ROLE_USER).build();
        when(roleService.getRoleByName("ROLE_USER")).thenReturn(Optional.of(role));

        mockMvc.perform(get("/roles/search").param("name", "ROLE_USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(RoleType.ROLE_USER));
    }

    @Test
    void getAllRoles_success() throws Exception {
        RoleDTO role1 = RoleDTO.builder().id(1L).name(RoleType.ROLE_USER).build();
        RoleDTO role2 = RoleDTO.builder().id(2L).name(RoleType.ROLE_ADMIN).build();

        when(roleService.getAllRoles()).thenReturn(List.of(role1, role2));

        mockMvc.perform(get("/roles/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void assignRoleToUser() throws Exception {
        UserDTO user = UserDTO.builder().id(1L).build();
        RoleDTO role = RoleDTO.builder().id(1L).name(RoleType.ROLE_USER).build();

        when(roleService.assignRoleToUser(1L, 1L)).thenReturn(Optional.of(role));

        mockMvc.perform(post("/roles/assign")
                .param("userId", "1")
                .param("roleId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ROLE_USER"));
    }

    @Test
    void removeRoleFromUser() throws Exception {
        UserDTO user = UserDTO.builder().id(1L).build();
        RoleDTO role = RoleDTO.builder().id(1L).name(RoleType.ROLE_USER).build();

        when(roleService.removeRoleFromUser(1L, 1L)).thenReturn(Optional.of(role));

        mockMvc.perform(post("/roles/remove")
                .param("userId", "1")
                .param("roleId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ROLE_USER"));
    }

}
