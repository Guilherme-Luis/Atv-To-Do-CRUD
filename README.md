# To-Do (CRUD)

Projeto avaliativo do 1º bimestre de Lab. Des. Multiplataforma - 6º DSM.

Backend de uma lista de tarefas feito em Java com Spring Boot e PostgreSQL. Permite criar, alterar e deletar tarefas. Não tem login nem usuário.

A tarefa tem nome, descrição, status, observações, data de criação e data de atualização. O status pode ser PENDENTE, EM_ANDAMENTO, CONCLUIDA ou CANCELADA.

## Banco de dados

Os scripts e os modelos conceitual e lógico estão na pasta `database`.

No pgAdmin, rodar primeiro o `01-criar-banco.sql` e depois, já conectado no banco `todolist`, rodar o `02-criar-tabelas.sql`.

A conexão fica no `application.properties` (porta 5433, usuário postgres, senha 123456). Se for diferente na sua máquina é só trocar lá.

## Como rodar

Importar no Eclipse como projeto Maven (File > Import > Existing Maven Projects) e rodar a classe `TodoApplication`.

## Testes

Os testes rodam com o profile `test`, que usa o H2 em memória, então não precisa do Postgres ligado. Para rodar: botão direito em `src/test/java` > Run As > JUnit Test.
