package com.exemplo.tarefas.service;

public class TarefaNaoEncontradaException extends RuntimeException {

    public TarefaNaoEncontradaException(Long id) {
        super("Tarefa nao encontrada com id: " + id);
    }
}
