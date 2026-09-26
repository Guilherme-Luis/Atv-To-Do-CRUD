package com.gui.todo.model.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.gui.todo.model.entity.Tarefa;
import com.gui.todo.model.enums.StatusTarefa;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class TarefaRepositoryTest {

	@Autowired
	TarefaRepository repository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	public void deveSalvarUmaTarefa() {
		Tarefa tarefa = criarTarefa();

		tarefa = repository.save(tarefa);

		Assertions.assertThat(tarefa.getId()).isNotNull();
	}

	@Test
	public void deveDeletarUmaTarefa() {
		Tarefa tarefa = criarEPersistirUmaTarefa();

		tarefa = entityManager.find(Tarefa.class, tarefa.getId());
		repository.delete(tarefa);

		Tarefa tarefaInexistente = entityManager.find(Tarefa.class, tarefa.getId());
		Assertions.assertThat(tarefaInexistente).isNull();
	}

	@Test
	public void deveAtualizarUmaTarefa() {
		Tarefa tarefa = criarEPersistirUmaTarefa();

		tarefa.setNome("Estudar Spring Boot");
		tarefa.setDescricao("Revisar os testes de integração");
		tarefa.setStatus(StatusTarefa.CONCLUIDA);
		tarefa.setObservacoes("Finalizado antes do prazo");
		repository.save(tarefa);

		Tarefa tarefaAtualizada = entityManager.find(Tarefa.class, tarefa.getId());
		Assertions.assertThat(tarefaAtualizada.getNome()).isEqualTo("Estudar Spring Boot");
		Assertions.assertThat(tarefaAtualizada.getDescricao()).isEqualTo("Revisar os testes de integração");
		Assertions.assertThat(tarefaAtualizada.getStatus()).isEqualTo(StatusTarefa.CONCLUIDA);
		Assertions.assertThat(tarefaAtualizada.getObservacoes()).isEqualTo("Finalizado antes do prazo");
	}

	@Test
	public void deveBuscarUmaTarefaPorId() {
		Tarefa tarefa = criarEPersistirUmaTarefa();

		Optional<Tarefa> tarefaEncontrada = repository.findById(tarefa.getId());

		Assertions.assertThat(tarefaEncontrada.isPresent()).isTrue();
	}

	@Test
	public void deveRetornarVazioAoBuscarTarefaInexistente() {
		Optional<Tarefa> result = repository.findById(999L);

		Assertions.assertThat(result.isPresent()).isFalse();
	}

	@Test
	public void deveBuscarTarefasPorStatus() {
		criarEPersistirUmaTarefa();
		Tarefa concluida = criarTarefa();
		concluida.setStatus(StatusTarefa.CONCLUIDA);
		entityManager.persist(concluida);

		List<Tarefa> pendentes = repository.findByStatus(StatusTarefa.PENDENTE);

		Assertions.assertThat(pendentes).hasSize(1);
		Assertions.assertThat(pendentes.get(0).getStatus()).isEqualTo(StatusTarefa.PENDENTE);
	}

	private Tarefa criarEPersistirUmaTarefa() {
		Tarefa tarefa = criarTarefa();
		entityManager.persist(tarefa);
		return tarefa;
	}

	public static Tarefa criarTarefa() {
		return Tarefa
				.builder()
				.nome("Estudar")
				.descricao("Estudar para a prova de Lab. Des. Multiplataforma")
				.status(StatusTarefa.PENDENTE)
				.observacoes("Capítulos 1 ao 3")
				.dataCriacao(LocalDateTime.now())
				.dataAtualizacao(LocalDateTime.now())
				.build();
	}
}
