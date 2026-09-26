package com.gui.todo.service;

import java.util.List;
import java.util.Optional;

import com.gui.todo.model.entity.Tarefa;
import com.gui.todo.model.enums.StatusTarefa;

public interface TarefaService {
	Tarefa salvar(Tarefa tarefa);

	Tarefa atualizar(Tarefa tarefa);

	void deletar(Tarefa tarefa);

	List<Tarefa> buscar(Tarefa tarefaFiltro);

	Optional<Tarefa> obterPorId(Long id);

	Tarefa atualizarStatus(Tarefa tarefa, StatusTarefa status);

	void validar(Tarefa tarefa);
}
