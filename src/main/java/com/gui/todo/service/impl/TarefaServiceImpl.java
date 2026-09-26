package com.gui.todo.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;

import com.gui.todo.exception.RegraNegocioException;
import com.gui.todo.model.entity.Tarefa;
import com.gui.todo.model.enums.StatusTarefa;
import com.gui.todo.model.repository.TarefaRepository;
import com.gui.todo.service.TarefaService;

import jakarta.transaction.Transactional;

@Service
public class TarefaServiceImpl implements TarefaService {

	private TarefaRepository repository;

	@Autowired
	public TarefaServiceImpl (TarefaRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	@Transactional
	public Tarefa salvar(Tarefa tarefa) {
		validar(tarefa);
		if(tarefa.getStatus() == null) {
			tarefa.setStatus(StatusTarefa.PENDENTE);
		}
		LocalDateTime agora = LocalDateTime.now();
		tarefa.setDataCriacao(agora);
		tarefa.setDataAtualizacao(agora);
		return repository.save(tarefa);
	}

	@Override
	@Transactional
	public Tarefa atualizar(Tarefa tarefa) {
		Objects.requireNonNull(tarefa.getId());
		validar(tarefa);
		Tarefa tarefaSalva = repository.findById(tarefa.getId())
				.orElseThrow(() -> new RegraNegocioException("Tarefa não encontrada na base de dados."));
		if(tarefa.getStatus() == null) {
			tarefa.setStatus(tarefaSalva.getStatus());
		}
		tarefa.setDataCriacao(tarefaSalva.getDataCriacao());
		tarefa.setDataAtualizacao(LocalDateTime.now());
		return repository.save(tarefa);
	}

	@Override
	@Transactional
	public void deletar(Tarefa tarefa) {
		Objects.requireNonNull(tarefa.getId());
		repository.delete(tarefa);
	}

	@Override
	public List<Tarefa> buscar(Tarefa tarefaFiltro) {
		Example<Tarefa> example = Example.of(tarefaFiltro,
				ExampleMatcher.matching()
				.withIgnoreCase()
				.withStringMatcher(StringMatcher.CONTAINING));
		return repository.findAll(example);
	}

	@Override
	public Optional<Tarefa> obterPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	@Transactional
	public Tarefa atualizarStatus(Tarefa tarefa, StatusTarefa status) {
		if(status == null) {
			throw new RegraNegocioException("Informe um Status válido.");
		}
		tarefa.setStatus(status);
		return atualizar(tarefa);
	}

	@Override
	public void validar(Tarefa tarefa) {
		if(tarefa.getNome() == null || tarefa.getNome().trim().equals("")) {
			throw new RegraNegocioException("Informe um Nome válido.");
		}
		if(tarefa.getNome().length() > 100) {
			throw new RegraNegocioException("O Nome deve ter no máximo 100 caracteres.");
		}
		if(tarefa.getDescricao() == null || tarefa.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma Descrição válida.");
		}
		if(tarefa.getDescricao().length() > 255) {
			throw new RegraNegocioException("A Descrição deve ter no máximo 255 caracteres.");
		}
		if(tarefa.getObservacoes() != null && tarefa.getObservacoes().length() > 500) {
			throw new RegraNegocioException("As Observações devem ter no máximo 500 caracteres.");
		}
	}

}
