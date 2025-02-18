package com.example.apiformatech.controller;

import com.example.apiformatech.model.Module;
import com.example.apiformatech.service.JwtService;
import com.example.apiformatech.service.ModuleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@WebMvcTest(ModuleController.class)
class ModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModuleService moduleService;

    @MockBean
    private JwtService jwtService;

    @Test
    void testCreateModule() throws Exception {
        Module module = new Module();
        module.setName("Java");

        when(moduleService.saveModule(any(Module.class))).thenReturn(module);

        mockMvc.perform(post("/api/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Java\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Java"));
    }

    @Test
    void testGetAllModules() throws Exception {
        when(moduleService.getAllModules()).thenReturn(List.of(new Module()));

        mockMvc.perform(get("/api/modules"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetModuleById_Success() throws Exception {
        Module module = new Module();
        module.setId(1L);
        module.setName("Java");

        when(moduleService.getModuleById(1L)).thenReturn(Optional.of(module));

        mockMvc.perform(get("/api/modules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Java"));
    }

    @Test
    void testGetModuleById_NotFound() throws Exception {
        when(moduleService.getModuleById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/modules/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteModule_Success() throws Exception {
        doNothing().when(moduleService).deleteModule(1L);

        mockMvc.perform(delete("/api/modules/1"))
                .andExpect(status().isNoContent());
    }
}
