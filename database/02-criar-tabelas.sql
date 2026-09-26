CREATE SCHEMA todo;

CREATE TABLE todo.tarefas
(
    id                BIGSERIAL    NOT NULL PRIMARY KEY,
    nome              VARCHAR(100) NOT NULL,
    descricao         VARCHAR(255) NOT NULL,
    status            VARCHAR(20)  NOT NULL CHECK (status IN ('PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA')),
    observacoes       VARCHAR(500),
    data_criacao      TIMESTAMP    NOT NULL DEFAULT NOW(),
    data_atualizacao  TIMESTAMP    NOT NULL DEFAULT NOW()
);
