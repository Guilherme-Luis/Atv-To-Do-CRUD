package com.gui.todo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarefaDTO {

	private Long id;
	private String nome;
	private String descricao;
	private String status;
	private String observacoes;

}
