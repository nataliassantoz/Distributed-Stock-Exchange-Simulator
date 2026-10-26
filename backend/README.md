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
AUTH_CSRF=$(jq -r .dados.token /tmp/stock-csrf.json)
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

Resposta de login: `{"cdRetorno":200,"msgTecnica":null,"msgUsuario":"Operação realizada com sucesso","dados":{"idSessao":"...","usuario":{"id":1,"nome":"Maria Silva","email":"maria@example.com"}}}`.
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

## Sessões ativas — SESSION-001

O login registra uma sessão autenticada com `idSessao`, `idUsuario`,
`dataHoraConexao` (UTC) e `status` (`ATIVA`). O identificador corresponde ao cookie
`JSESSIONID`; não é necessário criar uma sessão em uma chamada separada.

| Método | Endpoint | Resultado |
| --- | --- | --- |
| GET | `/api/sessoes/atual` | 200, dados da sessão atual |
| GET | `/api/sessoes` | 200, lista das sessões ativas do próprio usuário |
| POST | `/api/auth/logout` | 200, invalida a sessão atual, remove seu registro e apaga o cookie |

As consultas exigem autenticação; sem sessão válida retornam 401. O logout exige
CSRF, como os demais POSTs. Consulte `/api/auth/csrf` para obter o token antes de
enviá-lo. As respostas mantêm o envelope `ApiResult`.

Cada navegador/dispositivo pode manter uma sessão independente, inclusive para o
mesmo usuário. Logout de uma sessão preserva as demais. Novo login na mesma sessão
renova o identificador e substitui o registro anterior. Sessões anônimas criadas
para obter CSRF não entram no registro de participantes autenticados.

O registro usa memória local com acesso concorrente. Expiração por inatividade ou
invalidação remove a sessão pelo listener do contêiner. Não há histórico de sessões
encerradas, persistência distribuída ou recuperação após reinício nesta etapa.
Fechar o navegador ou perder a rede não encerra imediatamente a sessão: ela pode
permanecer válida até expirar. A remoção por timeout acompanha a verificação de
expiração do contêiner, não um monitoramento de presença em tempo real.

### Validação manual com dois investidores

1. Abra dois perfis de navegador ou dois clientes com armazenamentos de cookies separados.
2. Em cada cliente, obtenha CSRF, cadastre um usuário diferente e faça login.
3. Consulte `/api/sessoes/atual` nos dois: IDs de sessão e usuário devem ser diferentes.
4. Consulte `/api/auth/me` e `/api/sessoes` nos dois: ambos devem continuar autenticados e cada lista deve mostrar apenas suas próprias sessões.
5. Envie logout com CSRF no primeiro cliente. Suas consultas protegidas devem retornar 401; o segundo deve continuar recebendo 200.

Os testes `SessionTest`, `SessionServiceImplTest` e `SessionListenerTest` verificam
login simultâneo de dois usuários via HTTP, isolamento, logout com CSRF, troca de
identificador, múltiplas sessões por usuário, operações concorrentes e limpeza no
evento de destruição usado na expiração/invalidação. Execute `./mvnw test`.

## Respostas de erro

Os endpoints da API retornam `application/json` com o envelope `ApiResult<T>`:
`cdRetorno` corresponde ao status HTTP, `msgUsuario` contém a mensagem da operação,
`msgTecnica` fica nula nas respostas atuais e `dados` contém o resultado.
O service de ativos usa `ApiResultUtil<List<AtivoDTO.Response.Ativo>>`.

O tratamento global usa o mesmo envelope para erros, sem stack trace nem nomes
de classes internas. Por exemplo:

```json
{
  "cdRetorno": 409,
  "msgTecnica": null,
  "msgUsuario": "Email já cadastrado",
  "dados": null
}
```

Erros de validação incluem `dados.erros`, com as mensagens por campo, sem repetir os
valores enviados. Erros inesperados retornam 500 com mensagem genérica e são
registrados nos logs do servidor. Falhas de autenticação e acesso nos filtros do
Spring Security também usam o tratamento global.

## Investidores conectados — SESSION-002

`GET /mercado/investidores` exige autenticação pelo cookie `JSESSIONID`.
Retorna `200` com um item por investidor que possui pelo menos uma sessão
válida nesta instância, ordenado por `idUsuario`. Sem autenticação, retorna `401`.
A rota segue o contrato da issue #16, sem o prefixo `/api`.

```json
{
  "cdRetorno": 200,
  "msgTecnica": null,
  "msgUsuario": "Operação realizada com sucesso",
  "dados": [
    {
      "idUsuario": 1,
      "nome": "Maria Silva",
      "dataHoraConexao": "2026-10-22T12:00:00Z",
      "quantidadeSessoes": 2
    }
  ]
}
```

`dataHoraConexao` representa a conexão mais antiga entre as sessões ainda válidas
do investidor; `quantidadeSessoes` conta essas sessões. Identificadores de sessão,
email, senha e dados financeiros não são expostos. Uma consulta sem participantes
retorna `dados: []`.

Somente logins bem-sucedidos entram no registro. Logout remove a sessão atual;
o investidor permanece na lista enquanto outra sessão estiver válida. A consulta
exclui sessões invalidadas ou cujo prazo de inatividade já venceu, mesmo antes da
limpeza periódica do contêiner. Consultar a lista não renova a atividade dos demais
participantes. Fechar o navegador ou perder a rede não encerra imediatamente uma
sessão: conectado significa possuir uma sessão autenticada válida, sem heartbeat.
As sessões continuam em memória; não há histórico nem agregação entre instâncias.

### Validação

1. Cadastre e autentique dois investidores em navegadores ou clientes com cookies separados.
2. Consulte `/mercado/investidores`: os dois devem aparecer.
3. Autentique o primeiro investidor em outro cliente: ele deve aparecer uma vez, com duas sessões.
4. Faça logout de uma dessas sessões: o investidor deve continuar listado com uma sessão.
5. Faça logout da outra: apenas o segundo investidor deve aparecer.
6. Consulte sem autenticação: a resposta deve ser `401`.

Execute `mvn test` em `backend/`. `InvestidoresTest` valida o endpoint, autenticação,
agregação de sessões e logout; `SessionServiceImplTest` cobre exclusão de sessões
expiradas, invalidadas e removidas, lista vazia e sessões sem timeout.

## Registro de ordens — ORDER-001

`POST /ordens` exige sessão autenticada e token CSRF. Envie:

```json
{"ativo": 1, "tipo": "COMPRA", "quantidade": 10, "preco": 12.50}
```

`ativo` é o ID de um ativo existente; `tipo` aceita `COMPRA` ou `VENDA`.
Quantidade deve ser inteira e positiva. Preço deve ser positivo, com até 17
algarismos inteiros e duas casas decimais. O servidor define investidor pela
sessão, ID, status `PENDENTE` e `dataHora` em UTC. Retorna `201` com esses campos
em `dados`, no envelope `ApiResult`. Dados inválidos retornam `400`, ativo
inexistente `404`, ausência de autenticação com CSRF válido `401` e CSRF
inválido/ausente `403`.

Esta etapa registra solicitações: não valida disponibilidade nem reserva dinheiro
ou ações, não executa matching e não altera carteira, saldo ou preço do ativo.
`PENDENTE` indica uma solicitação persistida, não uma garantia de recursos para
execução. Validação financeira e reservas precisam ser implementadas antes de
habilitar o processamento de negociações. Quantidade original e restante são
inicialmente iguais; a prioridade futura pode desempatar `criada_em` pelo `id`.
Reenvios criam ordens independentes; não há chave de idempotência nesta etapa.

Validação manual: autentique-se conforme o fluxo acima, obtenha CSRF novamente
após o login e envie o JSON para `/ordens`. Repita com `VENDA`: ambas devem
retornar IDs distintos e status `PENDENTE`. Consulte a tabela `ordem` para conferir
a persistência. Quantidade zero, preço negativo e tipo inválido devem retornar
`400` sem criar registros. Execute `./mvnw test` para os testes automatizados.
