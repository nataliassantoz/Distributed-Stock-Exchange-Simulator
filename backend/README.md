# Backend — fundação Spring Boot

Requisitos: Java 21, Docker e Docker Compose. O Maven Wrapper está incluído.
Execute os comandos abaixo em `backend/`.

## Configuração e execução local

```bash
cp .env.example .env
# Edite .env e defina POSTGRES_PASSWORD antes de continuar.
docker compose up -d --wait postgres

# Bash: exporte as variáveis para o processo Java.
set -a
source .env
set +a
./mvnw spring-boot:run
```

Se já houver `.env`, preserve seu conteúdo. Compose lê esse arquivo automaticamente;
o Maven e o Java precisam das variáveis exportadas no terminal (ou configuradas na IDE).
No Windows, configure POSTGRES_PASSWORD no ambiente e use `mvnw.cmd`.

A API usa `localhost:8080`. O PostgreSQL usa `localhost:5433`, banco `stock_exchange`
e usuário `stock_user`. Overrides opcionais: `DB_URL`, `DB_USER` e `DB_PASSWORD`.
Sem DB_PASSWORD, a aplicação utiliza POSTGRES_PASSWORD, assim como o Compose.

## Validação

Em outro terminal:

```bash
curl --fail http://localhost:8080/actuator/health
# Esperado: HTTP 200 e campo "status": "UP"
docker compose exec postgres psql -U stock_user -d stock_exchange \
  -c 'SELECT * FROM flyway_schema_history;'
```

O health check inclui a disponibilidade do datasource. Somente o endpoint health
é exposto, sem detalhes internos. Capture a resposta no navegador para a evidência
solicitada pela issue #9.

O Flyway inicializa o histórico no banco. Ainda não existem entidades nem migrations
de negócio; os próximos scripts devem ser criados em `src/main/resources/db/migration`.
O Hibernate apenas valida o modelo (`ddl-auto: validate`).

O build padrão não exige PostgreSQL nem credenciais locais:

```bash
./mvnw clean install
```

Ainda não há testes unitários nesta fundação. Os dois testes existentes são de
integração e executam somente com o perfil Maven `integration`, pelo Failsafe.
Para validar inicialização, conexão real ao PostgreSQL, histórico do Flyway e HTTP:

```bash
docker compose up -d --wait postgres
set -a
source .env
set +a
# DB_PASSWORD tem prioridade; remova um valor antigo se quiser usar POSTGRES_PASSWORD.
unset DB_PASSWORD
./mvnw clean verify -Pintegration
```

O perfil exige banco disponível e credenciais válidas. Falhas de integração continuam
fazendo o comando falhar; os testes não são desativados nesse perfil.

Para parar o banco preservando os dados: `docker compose stop postgres`.
Se aparecer falha de autenticação, confira se a senha corresponde à usada quando o
volume foi inicializado. Alterar `.env` não altera a senha de um banco já existente.

## Estrutura e escopo

Os pacotes `api`, `core` e `domain` estão preservados com `package-info.java`.
As pastas reservadas ainda não contêm funcionalidades de negócio.
O Compose desta etapa executa o PostgreSQL; a aplicação roda via Maven/Java.
Para executar também a API em contêiner (sem iniciar via Maven):

```bash
docker compose --profile app up -d --build --wait
```

Se a porta 8080 estiver ocupada, use `APP_PORT=18080 docker compose --profile app up -d --build --wait`
e consulte `http://localhost:18080/actuator/health`.

O Dockerfile compila com Java 21 e executa o JAR em um JRE 21 com usuário sem privilégios.
O banco precisa estar saudável antes de a API iniciar. Valide a prontidão da API
consultando `/actuator/health`; o Compose não define healthcheck para a API.
Para parar os dois serviços: `docker compose --profile app stop`.
