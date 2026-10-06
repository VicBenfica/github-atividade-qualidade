package com.exemplo.tarefas.controller;

import com.exemplo.tarefas.model.Tarefa;
import com.exemplo.tarefas.service.TarefaNaoEncontradaException;
import com.exemplo.tarefas.service.TarefaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {TarefaController.class, ApiExceptionHandler.class})
class TarefaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TarefaService tarefaService;

    @Test
    @DisplayName("GET /api/tarefas - Deve retornar status 200 e lista de tarefas")
    void deveListarTodas() throws Exception {
        List<Tarefa> tarefas = List.of(new Tarefa(1L, "Aprender MockMvc", false));
        when(tarefaService.listarTodas()).thenReturn(tarefas);

        mockMvc.perform(get("/api/tarefas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Aprender MockMvc"))
                .andExpect(jsonPath("$[0].concluida").value(false));
    }

    @Test
    @DisplayName("POST /api/tarefas - Deve retornar 201 Created ao enviar payload valido")
    void deveCriarTarefa() throws Exception {
        Tarefa entrada = new Tarefa("Nova Tarefa", false);
        Tarefa criada = new Tarefa(1L, "Nova Tarefa", false);

        when(tarefaService.criar(any(Tarefa.class))).thenReturn(criada);

        mockMvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Nova Tarefa"));
    }

    @Test
    @DisplayName("POST /api/tarefas - Deve retornar 400 Bad Request ao enviar titulo em branco")
    void deveValidarTituloObrigatorio() throws Exception {
        Tarefa entradaInvalida = new Tarefa("", false);

        mockMvc.perform(post("/api/tarefas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entradaInvalida)))
                .andExpect(status().isBadRequest());

        verify(tarefaService, never()).criar(any());
    }

    @Test
    @DisplayName("PATCH /api/tarefas/{id}/concluir - Deve retornar status 200")
    void deveConcluirTarefa() throws Exception {
        Tarefa concluida = new Tarefa(1L, "Tarefa Concluida", true);
        when(tarefaService.concluir(1L)).thenReturn(concluida);

        mockMvc.perform(patch("/api/tarefas/1/concluir"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.concluida").value(true));
    }

    @Test
    @DisplayName("GET /api/tarefas/{id} - Deve retornar status 200 e a tarefa quando o ID existir")
    void deveBuscarPorIdExistente() throws Exception {
        Tarefa tarefa = new Tarefa(1L, "Estudar Spring Boot", false);
        when(tarefaService.buscarPorId(1L)).thenReturn(tarefa);

        mockMvc.perform(get("/api/tarefas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Estudar Spring Boot"))
                .andExpect(jsonPath("$.concluida").value(false));

        verify(tarefaService, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/tarefas/{id} - Deve acionar o ApiExceptionHandler e retornar 404 quando ID nao existir")
    void deveRetornar404ComMensagemDoHandler() throws Exception {
        when(tarefaService.buscarPorId(99L))
                .thenThrow(new TarefaNaoEncontradaException(99L));

        mockMvc.perform(get("/api/tarefas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Tarefa nao encontrada com id: 99"));
    }

    @Test
    @DisplayName("DELETE /api/tarefas/{id} - Deve retornar status 204 No Content")
    void deveDeletarTarefa() throws Exception {
        doNothing().when(tarefaService).deletar(1L);

        mockMvc.perform(delete("/api/tarefas/1"))
                .andExpect(status().isNoContent());

        verify(tarefaService).deletar(1L);
    }


}