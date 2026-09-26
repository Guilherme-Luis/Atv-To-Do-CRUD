package com.gui.todo.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gui.todo.exception.RegraNegocioException;
import com.gui.todo.model.entity.Tarefa;
import com.gui.todo.model.enums.StatusTarefa;
import com.gui.todo.model.repository.TarefaRepository;
import com.gui.todo.model.repository.TarefaRepositoryTest;
import com.gui.todo.service.impl.TarefaServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class TarefaServiceTest {

	@SpyBean
	TarefaServiceImpl service;

	@MockBean
	TarefaRepository repository;

	@Test
	public void deveSalvarUmaTarefa() {
		Tarefa tarefaASalvar = TarefaRepositoryTest.criarTarefa();
		doNothing().when(service).validar(tarefaASalvar);

		Tarefa tarefaSalva = TarefaRepositoryTest.criarTarefa();
		tarefaSalva.setId(1L);
		when(repository.save(tarefaASalvar)).thenReturn(tarefaSalva);

		Tarefa tarefa = service.salvar(tarefaASalvar);

		Assertions.assertThat(tarefa.getId()).isEqualTo(tarefaSalva.getId());
		Assertions.assertThat(tarefa.getStatus()).isEqualTo(StatusTarefa.PENDENTE);
	}

	@Test
	public void deveDefinirStatusPendenteEDatasAoSalvarTarefaSemStatus() {
		Tarefa tarefaASalvar = TarefaRepositoryTest.criarTarefa();
		tarefaASalvar.setStatus(null);
		tarefaASalvar.setDataCriacao(null);
		tarefaASalvar.setDataAtualizacao(null);
		when(repository.save(tarefaASalvar)).thenReturn(tarefaASalvar);

		Tarefa tarefa = service.salvar(tarefaASalvar);

		Assertions.assertThat(tarefa.getStatus()).isEqualTo(StatusTarefa.PENDENTE);
		Assertions.assertThat(tarefa.getDataCriacao()).isNotNull();
		Assertions.assertThat(tarefa.getDataAtualizacao()).isNotNull();
	}

	@Test
	public void naoDeveSalvarUmaTarefaQuandoHouverErroDeValidacao() {
		Tarefa tarefaASalvar = TarefaRepositoryTest.criarTarefa();
		doThrow(RegraNegocioException.class).when(service).validar(tarefaASalvar);

		Assertions.catchThrowableOfType(() -> service.salvar(tarefaASalvar), RegraNegocioException.class);

		verify(repository, never()).save(tarefaASalvar);
	}

	@Test
	public void deveAtualizarUmaTarefa() {
		LocalDateTime dataCriacao = LocalDateTime.of(2026, 9, 1, 10, 0);
		Tarefa tarefaExistente = TarefaRepositoryTest.criarTarefa();
		tarefaExistente.setId(1L);
		tarefaExistente.setDataCriacao(dataCriacao);

		Tarefa tarefaAtualizar = TarefaRepositoryTest.criarTarefa();
		tarefaAtualizar.setId(1L);
		tarefaAtualizar.setNome("Nome alterado");
		tarefaAtualizar.setDataCriacao(null);

		doNothing().when(service).validar(tarefaAtualizar);
		when(repository.findById(1L)).thenReturn(Optional.of(tarefaExistente));
		when(repository.save(tarefaAtualizar)).thenReturn(tarefaAtualizar);

		Tarefa tarefa = service.atualizar(tarefaAtualizar);

		verify(repository).save(tarefaAtualizar);
		Assertions.assertThat(tarefa.getNome()).isEqualTo("Nome alterado");
		Assertions.assertThat(tarefa.getDataCriacao()).isEqualTo(dataCriacao);
		Assertions.assertThat(tarefa.getDataAtualizacao()).isAfter(dataCriacao);
	}

	@Test
	public void deveLancarErroAoTentarAtualizarUmaTarefaQueAindaNaoFoiSalva() {
		Tarefa tarefa = TarefaRepositoryTest.criarTarefa();

		Assertions.catchThrowableOfType(() -> service.atualizar(tarefa), NullPointerException.class);

		verify(repository, never()).save(tarefa);
	}

	@Test
	public void deveLancarErroAoTentarAtualizarUmaTarefaInexistente() {
		Tarefa tarefa = TarefaRepositoryTest.criarTarefa();
		tarefa.setId(1L);
		when(repository.findById(1L)).thenReturn(Optional.empty());

		Throwable erro = Assertions.catchThrowable(() -> service.atualizar(tarefa));

		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Tarefa não encontrada na base de dados.");
		verify(repository, never()).save(tarefa);
	}

	@Test
	public void deveDeletarUmaTarefa() {
		Tarefa tarefa = TarefaRepositoryTest.criarTarefa();
		tarefa.setId(1L);

		service.deletar(tarefa);

		verify(repository).delete(tarefa);
	}

	@Test
	public void deveLancarErroAoTentarDeletarUmaTarefaQueAindaNaoFoiSalva() {
		Tarefa tarefa = TarefaRepositoryTest.criarTarefa();

		Assertions.catchThrowableOfType(() -> service.deletar(tarefa), NullPointerException.class);

		verify(repository, never()).delete(tarefa);
	}

	@Test
	public void deveFiltrarTarefas() {
		Tarefa tarefa = TarefaRepositoryTest.criarTarefa();
		tarefa.setId(1L);
		List<Tarefa> lista = Arrays.asList(tarefa);
		when(repository.findAll(any(Example.class))).thenReturn(lista);

		List<Tarefa> resultado = service.buscar(tarefa);

		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(tarefa);
	}

	@Test
	public void deveAtualizarOStatusDeUmaTarefa() {
		Tarefa tarefa = TarefaRepositoryTest.criarTarefa();
		tarefa.setId(1L);
		StatusTarefa novoStatus = StatusTarefa.CONCLUIDA;
		doNothing().when(service).validar(tarefa);
		when(repository.findById(1L)).thenReturn(Optional.of(tarefa));
		when(repository.save(tarefa)).thenReturn(tarefa);

		service.atualizarStatus(tarefa, novoStatus);

		Assertions.assertThat(tarefa.getStatus()).isEqualTo(novoStatus);
		verify(service).atualizar(tarefa);
	}

	@Test
	public void deveLancarErroAoAtualizarStatusNulo() {
		Tarefa tarefa = TarefaRepositoryTest.criarTarefa();
		tarefa.setId(1L);

		Throwable erro = Assertions.catchThrowable(() -> service.atualizarStatus(tarefa, null));

		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Status válido.");
		verify(repository, never()).save(tarefa);
	}

	@Test
	public void deveObterUmaTarefaPorId() {
		Long id = 1L;
		Tarefa tarefa = TarefaRepositoryTest.criarTarefa();
		tarefa.setId(id);
		when(repository.findById(id)).thenReturn(Optional.of(tarefa));

		Optional<Tarefa> resultado = service.obterPorId(id);

		Assertions.assertThat(resultado.isPresent()).isTrue();
	}

	@Test
	public void deveRetornarVazioQuandoATarefaNaoExiste() {
		Long id = 1L;
		when(repository.findById(id)).thenReturn(Optional.empty());

		Optional<Tarefa> resultado = service.obterPorId(id);

		Assertions.assertThat(resultado.isPresent()).isFalse();
	}

	@Test
	public void deveLancarErrosAoValidarUmaTarefa() {
		Tarefa tarefa = new Tarefa();

		Throwable erro = Assertions.catchThrowable(() -> service.validar(tarefa));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Nome válido.");

		tarefa.setNome("");

		erro = Assertions.catchThrowable(() -> service.validar(tarefa));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Nome válido.");

		tarefa.setNome("a".repeat(101));

		erro = Assertions.catchThrowable(() -> service.validar(tarefa));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("O Nome deve ter no máximo 100 caracteres.");

		tarefa.setNome("Estudar");

		erro = Assertions.catchThrowable(() -> service.validar(tarefa));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");

		tarefa.setDescricao("a".repeat(256));

		erro = Assertions.catchThrowable(() -> service.validar(tarefa));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("A Descrição deve ter no máximo 255 caracteres.");

		tarefa.setDescricao("Estudar para a prova");
		tarefa.setObservacoes("a".repeat(501));

		erro = Assertions.catchThrowable(() -> service.validar(tarefa));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("As Observações devem ter no máximo 500 caracteres.");

		tarefa.setObservacoes(null);

		erro = Assertions.catchThrowable(() -> service.validar(tarefa));
		Assertions.assertThat(erro).isNull();
	}
}
