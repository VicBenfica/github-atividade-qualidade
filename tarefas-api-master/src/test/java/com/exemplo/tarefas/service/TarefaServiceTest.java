package com.exemplo.tarefas.service;

import com.exemplo.tarefas.model.Tarefa;
import com.exemplo.tarefas.repository.TarefaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @InjectMocks
    private TarefaService tarefaService;

    @Nested
    @DisplayName("Testes de Consulta")
    class Consultas {

        @Test
        @DisplayName("Deve listar todas as tarefas")
        void deveListarTodas() {
            List<Tarefa> tarefas = List.of(
                    new Tarefa(1L, "Estudar Spring", false),
                    new Tarefa(2L, "Escrever testes", true)
            );
            when(tarefaRepository.findAll()).thenReturn(tarefas);

            List<Tarefa> resultado = tarefaService.listarTodas();

            assertThat(resultado).hasSize(2).isEqualTo(tarefas);
            verify(tarefaRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve buscar tarefa por ID existente")
        void deveBuscarPorIdExistente() {
            Tarefa tarefa = new Tarefa(1L, "Estudar Spring", false);
            when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

            Tarefa resultado = tarefaService.buscarPorId(1L);

            assertThat(resultado).isEqualTo(tarefa);
        }

        @Test
        @DisplayName("Deve lancar excecao ao buscar ID inexistente")
        void deveLancarExcecaoQuandoIdNaoEncontrado() {
            when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> tarefaService.buscarPorId(99L))
                    .isInstanceOf(TarefaNaoEncontradaException.class);
        }
    }

    @Nested
    @DisplayName("Testes de Regras de Negócio e Alteração")
    class Negocio {

        @Test
        @DisplayName("Deve criar nova tarefa garantindo que ID seja nulo e concluida seja false")
        void deveCriarTarefaComRegras() {
            Tarefa entrada = new Tarefa(10L, "Nova Tarefa", true);
            Tarefa salva = new Tarefa(1L, "Nova Tarefa", false);

            when(tarefaRepository.save(any(Tarefa.class))).thenReturn(salva);

            Tarefa resultado = tarefaService.criar(entrada);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.isConcluida()).isFalse();

            verify(tarefaRepository).save(argThat(t ->
                    t.getId() == null && !t.isConcluida() && t.getTitulo().equals("Nova Tarefa")
            ));
        }

        @Test
        @DisplayName("Deve concluir uma tarefa existente")
        void deveConcluirTarefa() {
            Tarefa existente = new Tarefa(1L, "Estudar JPA", false);
            when(tarefaRepository.findById(1L)).thenReturn(Optional.of(existente));
            when(tarefaRepository.save(any(Tarefa.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Tarefa resultado = tarefaService.concluir(1L);

            assertThat(resultado.isConcluida()).isTrue();
            verify(tarefaRepository).save(existente);
        }

        @Test
        @DisplayName("Deve deletar tarefa quando ID existir")
        void deveDeletarTarefaExistente() {
            when(tarefaRepository.existsById(1L)).thenReturn(true);
            doNothing().when(tarefaRepository).deleteById(1L);

            tarefaService.deletar(1L);

            verify(tarefaRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Deve lancar excecao ao tentar deletar ID inexistente")
        void deveLancarExcecaoAoDeletarInexistente() {
            when(tarefaRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> tarefaService.deletar(99L))
                    .isInstanceOf(TarefaNaoEncontradaException.class);

            verify(tarefaRepository, never()).deleteById(anyLong());
        }
    }


}