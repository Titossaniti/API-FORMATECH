package com.example.apiformatech.service;

import com.example.apiformatech.exception.ResourceNotFoundException;
import com.example.apiformatech.model.Module;
import com.example.apiformatech.repository.ModuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModuleServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private ModuleService moduleService;

    private Module testModule;

    @BeforeEach
    void setUp() {
        testModule = new Module();
        testModule.setId(1L);
        testModule.setName("Java");
        testModule.setDescription("Introduction to Java");
    }

    @Test
    void testSaveModule() {
        when(moduleRepository.save(any(Module.class))).thenReturn(testModule);

        Module savedModule = moduleService.saveModule(testModule);

        assertNotNull(savedModule);
        assertEquals("Java", savedModule.getName());
        verify(moduleRepository, times(1)).save(testModule);
    }

    @Test
    void testGetAllModules() {
        when(moduleRepository.findAll()).thenReturn(List.of(testModule));

        List<Module> modules = moduleService.getAllModules();

        assertFalse(modules.isEmpty());
        assertEquals(1, modules.size());
        verify(moduleRepository, times(1)).findAll();
    }

    @Test
    void testGetModuleById_Found() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));

        Optional<Module> module = moduleService.getModuleById(1L);

        assertTrue(module.isPresent());
        assertEquals("Java", module.get().getName());
    }

    @Test
    void testGetModuleById_NotFound() {
        when(moduleRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Module> module = moduleService.getModuleById(999L);

        assertFalse(module.isPresent());
    }

    @Test
    void testUpdateModule_Success() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));
        when(moduleRepository.save(any(Module.class))).thenReturn(testModule);

        Module updatedModule = new Module();
        updatedModule.setName("Java Avancé");
        updatedModule.setDescription("Approfondir Java");

        Module result = moduleService.updateModule(1L, updatedModule);

        assertNotNull(result);
        assertEquals("Java Avancé", result.getName());
    }

    @Test
    void testUpdateModule_NotFound() {
        when(moduleRepository.findById(999L)).thenReturn(Optional.empty());

        Module updatedModule = new Module();

        assertThrows(ResourceNotFoundException.class, () -> moduleService.updateModule(999L, updatedModule));
    }

    @Test
    void testDeleteModule_Success() {
        when(moduleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(moduleRepository).deleteById(1L);

        assertDoesNotThrow(() -> moduleService.deleteModule(1L));
        verify(moduleRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteModule_NotFound() {
        when(moduleRepository.existsById(999L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> moduleService.deleteModule(999L));
    }
}
