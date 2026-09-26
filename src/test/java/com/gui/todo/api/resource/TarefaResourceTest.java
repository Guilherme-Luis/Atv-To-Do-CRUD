package com.gui.todo.api.resource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gui.todo.api.dto.TarefaDTO;
import com.gui.todo.exception.RegraNegocioException;
import com.gui.todo.model.entity.Tarefa;
import com.gui.todo.model.enums.StatusTarefa;
import com.gui.todo.service.TarefaService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = TarefaResource.class)
@AutoConfigureMockMvc
public class TarefaResourceTest {

	static final String API = "/api/tarefas";
	static final MediaType JSON = MediaType.APPLICATION_JSON;

	@Autowired
	MockMvc mvc;

	@MockBean
	TarefaService service;

	@Test
	public void deveCriarUmaTarefa() throws Exception {
		TarefaDTO dto = TarefaDTO.builder()
				.nome("Estudar")
				.descricao("Estudar para a prova")
				.build();

		Tarefa tarefa = Tarefa.builder()
				.id(1L)
				.nome("Estudar")
				.descricao("Estudar para a prova")
				.status(StatusTarefa.PENDENTE)
				.build();

		when(service.salvar(any(Tarefa.class))).thenReturn(tarefa);
		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(API)
				.accept(JSON)
				.contentType(JSON)
				.content(json);

		mvc.perform(request)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("id").value(tarefa.getId()))
				.andExpect(jsonPath("nome").value(tarefa.getNome()))
				.andExpect(jsonPath("status").value("PENDENTE"));
	}

	@Test
	public void deveRetornarBadRequestAoCriarTarefaInvalida() throws Exception {
		TarefaDTO dto = TarefaDTO.builder().nome("").build();

		when(service.salvar(any(Tarefa.class))).thenThrow(new RegraNegocioException("Informe um Nome válido."));
		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(API)
				.accept(JSON)
				.contentType(JSON)
				.content(json);

		mvc.perform(request)
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Informe um Nome válido."));
	}

	@Test
	public void deveAtualizarUmaTarefa() throws Exception {
		TarefaDTO dto = TarefaDTO.builder()
				.nome("Estudar Java")
				.descricao("Estudar para a prova")
				.status("EM_ANDAMENTO")
				.build();

		Tarefa tarefa = Tarefa.builder().id(1L).nome("Estudar").descricao("Estudar para a prova").build();

		when(service.obterPorId(1L)).thenReturn(Optional.of(tarefa));
		String json = new ObjectMapper().writeValueAsString(dto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.put(API.concat("/1"))
				.accept(JSON)
				.contentType(JSON)
				.content(json);

		mvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(1L))
				.andExpect(jsonPath("nome").value("Estudar Java"))
				.andExpect(jsonPath("status").value("EM_ANDAMENTO"));
	}

	@Test
	public void deveDeletarUmaTarefa() throws Exception {
		Tarefa tarefa = Tarefa.builder().id(1L).nome("Estudar").descricao("Estudar para a prova").build();

		when(service.obterPorId(1L)).thenReturn(Optional.of(tarefa));

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(API.concat("/1"))
				.accept(JSON);

		mvc.perform(request).andExpect(status().isNoContent());

		verify(service).deletar(tarefa);
	}

	@Test
	public void deveRetornarBadRequestAoDeletarTarefaInexistente() throws Exception {
		when(service.obterPorId(1L)).thenReturn(Optional.empty());

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.delete(API.concat("/1"))
				.accept(JSON);

		mvc.perform(request).andExpect(status().isBadRequest());
	}
}
