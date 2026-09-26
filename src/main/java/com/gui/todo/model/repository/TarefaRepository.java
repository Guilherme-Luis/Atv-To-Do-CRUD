package com.gui.todo.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gui.todo.model.entity.Tarefa;
import com.gui.todo.model.enums.StatusTarefa;

public interface TarefaRepository extends JpaRepository<Tarefa, Long>{
	List<Tarefa> findByStatus (StatusTarefa status);
}
