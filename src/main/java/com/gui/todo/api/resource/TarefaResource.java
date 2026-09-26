package com.gui.todo.api.resource;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gui.todo.api.dto.AtualizaStatusDTO;
import com.gui.todo.api.dto.TarefaDTO;
import com.gui.todo.exception.RegraNegocioException;
import com.gui.todo.model.entity.Tarefa;
import com.gui.todo.model.enums.StatusTarefa;
import com.gui.todo.service.TarefaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tarefas")
@RequiredArgsConstructor
public class TarefaResource {

	private final TarefaService service;

	@GetMapping
	public ResponseEntity buscar(
			@RequestParam(value = "nome", required = false) String nome,
			@RequestParam(value = "status", required = false) String status) {
		Tarefa tarefaFiltro = new Tarefa();
		tarefaFiltro.setNome(nome);
		if(status != null) {
			try {
				tarefaFiltro.setStatus(StatusTarefa.valueOf(status));
			} catch (IllegalArgumentException e) {
				return ResponseEntity.badRequest().body("Status inválido.");
			}
		}
		List<Tarefa> tarefas = service.buscar(tarefaFiltro);
		return ResponseEntity.ok(tarefas);
	}

	@GetMapping("{id}")
	public ResponseEntity obterTarefa(@PathVariable("id") Long id) {
		return service.obterPorId(id)
				.map(tarefa -> new ResponseEntity(tarefa, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
	}

	@PostMapping
	public ResponseEntity salvar(@RequestBody TarefaDTO dto) {
		try {
			Tarefa entidade = converter(dto);
			entidade = service.salvar(entidade);
			return new ResponseEntity(entidade, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody TarefaDTO dto) {
		return service.obterPorId(id).map(entity -> {
			try {
				Tarefa tarefa = converter(dto);
				tarefa.setId(entity.getId());
				service.atualizar(tarefa);
				return ResponseEntity.ok(tarefa);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Tarefa não encontrada na base de dados.", HttpStatus.BAD_REQUEST));
	}

	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
		return service.obterPorId(id).map(entity -> {
			try {
				StatusTarefa statusSelecionado = StatusTarefa.valueOf(dto.getStatus());
				service.atualizarStatus(entity, statusSelecionado);
				return ResponseEntity.ok(entity);
			} catch (IllegalArgumentException | NullPointerException e) {
				return ResponseEntity.badRequest().body("Não foi possível atualizar o status da tarefa, envie um status válido.");
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Tarefa não encontrada na base de dados.", HttpStatus.BAD_REQUEST));
	}

	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return service.obterPorId(id).map(entity -> {
			service.deletar(entity);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> new ResponseEntity("Tarefa não encontrada na base de dados.", HttpStatus.BAD_REQUEST));
	}

	private Tarefa converter(TarefaDTO dto) {
		Tarefa tarefa = new Tarefa();
		tarefa.setId(dto.getId());
		tarefa.setNome(dto.getNome());
		tarefa.setDescricao(dto.getDescricao());
		tarefa.setObservacoes(dto.getObservacoes());
		if(dto.getStatus() != null) {
			try {
				tarefa.setStatus(StatusTarefa.valueOf(dto.getStatus()));
			} catch (IllegalArgumentException e) {
				throw new RegraNegocioException("Status inválido.");
			}
		}
		return tarefa;
	}

}
