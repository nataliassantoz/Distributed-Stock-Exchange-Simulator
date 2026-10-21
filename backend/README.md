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

O Flyway aplica a migration inicial com usuários, carteiras e ativos em
`src/main/resources/db/migration`.
O Hibernate apenas valida o modelo (`ddl-auto: validate`).

O build padrão não exige PostgreSQL nem credenciais locais:

```bash
./mvnw clean install
```

Os testes de autenticação executam no build padrão com H2, migrations reais e servidor
HTTP local, sem exigir PostgreSQL. Os testes de infraestrutura PostgreSQL executam
somente com o perfil Maven `integration`, pelo Failsafe.
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
A autenticação usa as camadas de controller, DTO, serviço e repositório.
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

## Autenticação de usuários

Os DTOs seguem o contêiner `UsuarioDTO`, com interfaces de validação e classes
`Request.Cadastro`, `Request.Login`, `Response.Usuario` e `Response.Sessao`.

| Método | Endpoint | Resultado |
| --- | --- | --- |
| GET | `/api/auth/csrf` | Token CSRF e cookie inicial |
| POST | `/api/auth/cadastro` | 201, usuário com ID único e carteira criada |
| POST | `/api/auth/login` | 200, `idSessao` e `usuario`; renova cookie JSESSIONID |
| GET | `/api/auth/me` | 200, usuário autenticado; 401 sem sessão válida |

Cadastro recebe `{"nome":"Maria Silva","email":"maria@example.com","senha":"Senha123!"}`.
Login recebe `{"email":"maria@example.com","senha":"Senha123!"}`.
O email é normalizado para minúsculas; duplicidade retorna 409. Dados inválidos
retornam 400, credenciais incorretas retornam 401 e ausência de CSRF nos POSTs retorna 403.
As senhas usam BCrypt e nunca são incluídas nas respostas. O cadastro cria usuários
`HUMANO` com saldo zero; o cliente não escolhe o tipo nem o saldo.

Exemplo com curl e jq (API em execução):

```bash
# Preserve o cookie e envie o token nos POSTs.
curl -s -c /tmp/stock-cookies http://localhost:8080/api/auth/csrf > /tmp/stock-csrf.json
AUTH_CSRF=$(jq -r .token /tmp/stock-csrf.json)
curl -i -b /tmp/stock-cookies -c /tmp/stock-cookies \
  -H 'Content-Type: application/json' -H "X-CSRF-TOKEN: $AUTH_CSRF" \
  -d '{"nome":"Maria Silva","email":"maria@example.com","senha":"Senha123!"}' \
  http://localhost:8080/api/auth/cadastro
curl -i -b /tmp/stock-cookies -c /tmp/stock-cookies \
  -H 'Content-Type: application/json' -H "X-CSRF-TOKEN: $AUTH_CSRF" \
  -d '{"email":"maria@example.com","senha":"Senha123!"}' \
  http://localhost:8080/api/auth/login
curl -i -b /tmp/stock-cookies http://localhost:8080/api/auth/me
```

Resposta de login: `{"idSessao":"...","usuario":{"id":1,"nome":"Maria Silva","email":"maria@example.com"}}`.
O transporte da sessão nas próximas chamadas é o cookie `JSESSIONID`. O ID retornado
no JSON identifica essa mesma sessão. O usuário autenticado fica disponível no
`Authentication` do Spring Security, com `getName()` contendo o ID do usuário.
Novos endpoints exigem autenticação por padrão.

A sessão expira após 30 minutos de inatividade e fica na memória da instância:
reiniciar a aplicação encerra as sessões; múltiplas instâncias exigirão um repositório
compartilhado. O cookie usa HttpOnly e SameSite=Strict. Em HTTPS, configure
`SESSION_COOKIE_SECURE=true`. Senhas aceitam de 8 a 72 caracteres, limitadas também
a 72 bytes UTF-8 pelo BCrypt.

A collection para testar este fluxo no Bruno está em [`bruno/`](bruno/README.md).
Abra a pasta como collection, selecione **Local** e execute as requisições em ordem.

## Respostas de erro

O tratamento global retorna `application/problem+json`, com `status`, `title` e
`detail`, sem stack trace nem nomes de classes internas. Por exemplo:

```json
{
  "type": "about:blank",
  "title": "Conflict",
  "status": 409,
  "detail": "Email já cadastrado",
  "instance": "/api/auth/cadastro"
}
```

Erros de validação incluem `erros`, com as mensagens por campo, sem repetir os
valores enviados. Erros inesperados retornam 500 com mensagem genérica e são
registrados nos logs do servidor. Falhas de autenticação e acesso nos filtros do
Spring Security também usam o tratamento global.
