# ✔️ To-Do (CRUD) - Lista de Tarefas - Backend

Projeto avaliativo do 1º bimestre de Laboratório de Desenvolvimento Multiplataforma (6º DSM - Fatec Franca).

## 💻 Sobre o projeto

Aplicação para o gerenciamento de tarefas do dia a dia. Não possui sistema de login nem conceito de usuário.

## 🚀 Entidade

**Tarefa**

| Atributo          | Coluna no banco    | Tipo           |
|-------------------|--------------------|----------------|
| id                | id                 | BIGSERIAL (PK) |
| nome              | nome               | VARCHAR(100)   |
| descricao         | descricao          | VARCHAR(255)   |
| status            | status             | VARCHAR(20)    |
| observacoes       | observacoes        | VARCHAR(500)   |
| dataCriacao       | data_criacao       | TIMESTAMP      |
| dataAtualizacao   | data_atualizacao   | TIMESTAMP      |

Status possíveis: `PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`.

## 🚧 Requisitos

- Permitir criar a Tarefa
- Permitir alterar a Tarefa
- Permitir deletar a Tarefa

## 🛠 Tecnologias utilizadas

- Java 17
- Spring Boot 3.2.8 (Data JPA)
- PostgreSQL
- Lombok
- H2 (banco em memória usado nos testes)
- JUnit 5

## 🎲 Criando o banco de dados (pgAdmin 4)

Os scripts e os modelos do banco ficam na pasta [`database`](database):

- `01-criar-banco.sql` - criação do banco `todolist`
- `02-criar-tabelas.sql` - criação do schema `todo` e da tabela `tarefas`
- `modelo-conceitual.png` - modelo conceitual
- `modelo-logico.png` - modelo lógico

Para criar:

1. No pgAdmin, abra o Query Tool conectado no banco `postgres` e execute [`01-criar-banco.sql`](database/01-criar-banco.sql).
2. Dê refresh em *Databases*, abra o Query Tool no banco `todolist` e execute [`02-criar-tabelas.sql`](database/02-criar-tabelas.sql).
3. A tabela fica em `todolist > Schemas > todo > Tables > tarefas`. Para ver o diagrama pelo pgAdmin: botão direito no banco `todolist` > **ERD For Database**.

A conexão está configurada em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/todolist
spring.datasource.username=postgres
spring.datasource.password=123456
```

Se o seu PostgreSQL estiver em outra porta ou com outra senha, é só alterar esses valores.

## ▶️ Executando no Eclipse

1. `File > Import > Maven > Existing Maven Projects` e selecione a pasta do projeto.
2. Execute a classe `TodoApplication` com `Run As > Java Application` (ou `Spring Boot App`, se estiver usando o STS).
3. A aplicação sobe conectando no banco `todolist`.

Pelo terminal também é possível executar com:

```bash
./mvnw spring-boot:run
```

## ⚙️ Serviço

As regras ficam em `TarefaServiceImpl`:

- `salvar` - cria a tarefa; sem status ela fica como `PENDENTE` e as datas de criação e atualização são preenchidas automaticamente
- `atualizar` - altera a tarefa, mantendo a data de criação e atualizando a data de atualização
- `deletar` - deleta a tarefa
- `atualizarStatus` - altera somente o status
- `buscar` / `obterPorId` - consultas
- `validar` - valida nome, descrição e observações

## ✅ Testes

Os testes usam o profile `test`, que roda em cima do H2 em memória, então não precisam do PostgreSQL ligado.

- `TarefaRepositoryTest` - testes de integração do repositório com o banco
- `TarefaServiceTest` - testes do serviço (regras de negócio e validações)

No Eclipse: botão direito em `src/test/java` > `Run As > JUnit Test`. Pelo terminal:

```bash
./mvnw test
```
