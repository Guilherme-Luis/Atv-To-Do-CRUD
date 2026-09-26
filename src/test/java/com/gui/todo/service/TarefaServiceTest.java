package com.gui.todo.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gui.todo.exception.RegraNegocioException;
import com.gui.todo.model.entity.Tarefa;
import com.gui.todo.model.enums.StatusTarefa;
import com.gui.todo.model.repository.TarefaRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class TarefaServiceTest {

	@Autowired
	TarefaService service;

	@Autowired
	TarefaRepository repository;

	@Test
	public void deveSalvarUmaTarefa() {
		repository.deleteAll();
		Tarefa tarefa = criarTarefa();

		Tarefa tarefaSalva = service.salvar(tarefa);

		Assertions.assertNotNull(tarefaSalva.getId());
		Assertions.assertEquals(StatusTarefa.PENDENTE, tarefaSalva.getStatus());
		Assertions.assertNotNull(tarefaSalva.getDataCriacao());
		Assertions.assertNotNull(tarefaSalva.getDataAtualizacao());
	}

	@Test
	public void naoDeveSalvarUmaTarefaSemNome() {
		Tarefa tarefa = criarTarefa();
		tarefa.setNome("");

		Assertions.assertThrows(RegraNegocioException.class,
				() -> service.salvar(tarefa));
	}

	@Test
	public void deveAtualizarUmaTarefa() {
		Tarefa tarefaSalva = service.salvar(criarTarefa());
		Tarefa tarefaOriginal = repository.findById(tarefaSalva.getId()).get();

		Tarefa tarefa = criarTarefa();
		tarefa.setId(tarefaSalva.getId());
		tarefa.setNome("Estudar Spring Boot");
		tarefa.setStatus(StatusTarefa.EM_ANDAMENTO);
		service.atualizar(tarefa);

		Tarefa tarefaAtualizada = repository.findById(tarefaSalva.getId()).get();
		Assertions.assertEquals("Estudar Spring Boot", tarefaAtualizada.getNome());
		Assertions.assertEquals(StatusTarefa.EM_ANDAMENTO, tarefaAtualizada.getStatus());
		Assertions.assertEquals(tarefaOriginal.getDataCriacao(), tarefaAtualizada.getDataCriacao());
	}

	@Test
	public void deveLancarErroAoAtualizarUmaTarefaSemId() {
		Tarefa tarefa = criarTarefa();

		Assertions.assertThrows(NullPointerException.class,
				() -> service.atualizar(tarefa));
	}

	@Test
	public void deveLancarErroAoAtualizarUmaTarefaInexistente() {
		Tarefa tarefa = criarTarefa();
		tarefa.setId(999L);

		Assertions.assertThrows(RegraNegocioException.class,
				() -> service.atualizar(tarefa));
	}

	@Test
	public void deveDeletarUmaTarefa() {
		Tarefa tarefaSalva = service.salvar(criarTarefa());

		service.deletar(tarefaSalva);

		Optional<Tarefa> result = repository.findById(tarefaSalva.getId());
		Assertions.assertFalse(result.isPresent());
	}

	@Test
	public void deveLancarErroAoDeletarUmaTarefaSemId() {
		Tarefa tarefa = criarTarefa();

		Assertions.assertThrows(NullPointerException.class,
				() -> service.deletar(tarefa));
	}

	@Test
	public void deveAtualizarOStatusDeUmaTarefa() {
		Tarefa tarefaSalva = service.salvar(criarTarefa());

		service.atualizarStatus(tarefaSalva, StatusTarefa.CONCLUIDA);

		Tarefa tarefaAtualizada = repository.findById(tarefaSalva.getId()).get();
		Assertions.assertEquals(StatusTarefa.CONCLUIDA, tarefaAtualizada.getStatus());
	}

	@Test
	public void deveLancarErroAoAtualizarStatusNulo() {
		Tarefa tarefaSalva = service.salvar(criarTarefa());

		Assertions.assertThrows(RegraNegocioException.class,
				() -> service.atualizarStatus(tarefaSalva, null));
	}

	@Test
	public void deveBuscarTarefasPeloNome() {
		repository.deleteAll();
		service.salvar(criarTarefa());
		Tarefa outraTarefa = criarTarefa();
		outraTarefa.setNome("Mercado");
		service.salvar(outraTarefa);

		Tarefa filtro = new Tarefa();
		filtro.setNome("estud");
		List<Tarefa> result = service.buscar(filtro);

		Assertions.assertEquals(1, result.size());
		Assertions.assertEquals("Estudar", result.get(0).getNome());
	}

	@Test
	public void deveObterUmaTarefaPorId() {
		Tarefa tarefaSalva = service.salvar(criarTarefa());

		Optional<Tarefa> result = service.obterPorId(tarefaSalva.getId());

		Assertions.assertTrue(result.isPresent());
	}

	@Test
	public void deveValidarUmaTarefa() {
		Tarefa tarefa = criarTarefa();

		Assertions.assertDoesNotThrow(() -> service.validar(tarefa));
	}

	@Test
	public void deveLancarErroAoValidarTarefaSemDescricao() {
		Tarefa tarefa = criarTarefa();
		tarefa.setDescricao(null);

		Assertions.assertThrows(RegraNegocioException.class,
				() -> service.validar(tarefa));
	}

	@Test
	public void deveLancarErroAoValidarTarefaComNomeMaiorQueOPermitido() {
		Tarefa tarefa = criarTarefa();
		tarefa.setNome("a".repeat(101));

		Assertions.assertThrows(RegraNegocioException.class,
				() -> service.validar(tarefa));
	}

	@Test
	public void deveLancarErroAoValidarTarefaComObservacoesMaiorQueOPermitido() {
		Tarefa tarefa = criarTarefa();
		tarefa.setObservacoes("a".repeat(501));

		Assertions.assertThrows(RegraNegocioException.class,
				() -> service.validar(tarefa));
	}

	public static Tarefa criarTarefa() {
		return Tarefa
				.builder()
				.nome("Estudar")
				.descricao("Estudar para a prova de Lab. Des. Multiplataforma")
				.observacoes("Capítulos 1 ao 3")
				.build();
	}
}
