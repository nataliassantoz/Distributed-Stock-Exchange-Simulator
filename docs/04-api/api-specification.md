# Especificação inicial da API REST

## 1. Objetivo e escopo

Estabelecer o contrato de comunicação entre o backend, o Home Broker e os demais
clientes, conforme a [issue #13 — DOC-003](https://github.com/nataliassantoz/Distributed-Stock-Exchange-Simulator/issues/13).
Esta versão descreve os endpoints implementados de cadastro, autenticação,
identificação do usuário, consulta de ativos e verificação de saúde.

A implementação atual é a referência para URLs, payloads e validações. Os caminhos
simplificados apresentados na issue correspondem às seguintes rotas reais:

| Exemplo da issue | Endpoint implementado |
| --- | --- |
| `POST /usuarios` | `POST /api/auth/cadastro` |
| `POST /auth/login` | `POST /api/auth/login` |
| `GET /ativos` | `GET /api/ativos` |
| `GET /actuator/health` | `GET /actuator/health` |

Os caminhos simplificados não são aliases disponíveis. A documentação não altera
os controllers nem introduz novas rotas.

## 2. Convenções

- Origem local padrão: `http://localhost:8080`.
- Endpoints de negócio usam o prefixo `/api`, sem versão no caminho atualmente.
- Requisições POST usam `Content-Type: application/json`; respostas usam
  `application/json`.
- Requisições GET não precisam de corpo.
- O status HTTP é preservado e também aparece em `cdRetorno` no envelope.
- Preços são números JSON em reais, representados no backend por `BigDecimal` e
  persistidos com duas casas decimais. Não são strings com vírgula decimal.
- Identificadores de usuário são números inteiros; códigos de ativos são strings.
- Valores nos exemplos são ilustrativos e não representam registros pré-carregados.

### 2.1. Envelope de resposta: `ApiResult<T>`

Todos os endpoints de negócio documentados, incluindo CSRF e autenticação, usam:

| Campo | Tipo | Significado |
| --- | --- | --- |
| `cdRetorno` | integer | Código igual ao status HTTP da resposta |
| `msgTecnica` | string ou null | Campo reservado para mensagem técnica; atualmente retorna `null` |
| `msgUsuario` | string | Mensagem destinada ao consumidor |
| `dados` | objeto, array ou null | Resultado da operação ou detalhes de validação |

A mensagem padrão de sucesso é `Operação realizada com sucesso`.
O controller de ativos delega à interface `AtivosService`; `AtivosServiceImpl`
consulta o repositório e utiliza `ApiResultUtil` para construir o envelope.

**Exceção:** `/actuator/health` é um endpoint de infraestrutura do Spring Boot
Actuator e mantém o formato próprio `{"status":"UP"}`, sem `ApiResult`.

### 2.2. Erros

Erros tratados pela API usam o mesmo envelope e mantêm o status HTTP correspondente:

```json
{
  "cdRetorno": 401,
  "msgTecnica": null,
  "msgUsuario": "Autenticação necessária. Faça login para continuar.",
  "dados": null
}
```

Erros de validação de campos incluem as mensagens em `dados.erros`:

```json
{
  "cdRetorno": 400,
  "msgTecnica": null,
  "msgUsuario": "Dados inválidos",
  "dados": {
    "erros": {
      "nome": "Nome deve ter entre 3 e 150 caracteres",
      "email": "Email inválido",
      "senha": "Senha deve ter entre 8 e 72 caracteres"
    }
  }
}
```

Quando várias restrições falham no mesmo campo, apenas uma mensagem por campo é
retornada; o cliente não deve depender da ordem das mensagens.
Validações adicionais no service podem retornar `400` com uma mensagem específica
em `msgUsuario` e `dados: null`.

| Status | Situação | Mensagem ou comportamento |
| --- | --- | --- |
| `400` | Campos inválidos | `Dados inválidos`, com `dados.erros` |
| `400` | Corpo ausente ou JSON inválido | `Corpo da requisição ausente ou JSON inválido` |
| `401` | Sessão ausente, inválida ou expirada em rota protegida | `Autenticação necessária. Faça login para continuar.` |
| `401` | Email inexistente ou senha incorreta no login | `Credenciais inválidas` |
| `403` | CSRF ausente/inválido em POST ou acesso negado | `Acesso negado. Verifique suas permissões e o token CSRF.` |
| `409` | Email já cadastrado | `Email já cadastrado` |
| `500` | Erro inesperado no backend | `Ocorreu um erro interno. Tente novamente mais tarde.` |

Falhas de segurança podem ser retornadas antes da validação do corpo. Por exemplo,
um POST sem CSRF pode retornar `403` mesmo contendo campos inválidos.
Erros inesperados são registrados nos logs; respostas não expõem stack traces,
nomes de classes internas, senhas ou hashes.

## 3. Endpoints disponíveis

| Método | URL | Sessão autenticada | CSRF | Sucesso |
| --- | --- | --- | --- | --- |
| GET | `/api/auth/csrf` | Não | Não | `200` |
| POST | `/api/auth/cadastro` | Não | Sim | `201` |
| POST | `/api/auth/login` | Não | Sim | `200` |
| GET | `/api/auth/me` | Sim | Não | `200` |
| GET | `/api/ativos` | Sim | Não | `200` |
| GET | `/actuator/health` | Não | Não | `200` quando saudável |

### 3.1. Obter token CSRF

**Objetivo:** obter o token necessário para os POSTs e estabelecer o contexto de
sessão inicial.

**Método e URL:** `GET /api/auth/csrf`.

**Request:** sem corpo ou parâmetros obrigatórios. Preservar os cookies recebidos.

**Response — HTTP 200:**

```json
{
  "cdRetorno": 200,
  "msgTecnica": null,
  "msgUsuario": "Operação realizada com sucesso",
  "dados": {
    "parameterName": "_csrf",
    "token": "token-csrf-retornado-pelo-servidor",
    "headerName": "X-CSRF-TOKEN"
  }
}
```

Usar `dados.headerName` como nome do header e `dados.token` como seu valor nos POSTs,
com os cookies da mesma sessão. O token CSRF não substitui a autenticação.

**Possíveis erros:** `500` em falha inesperada.

### 3.2. Cadastrar usuário

**Objetivo:** cadastrar um investidor humano e criar sua carteira.

**Método e URL:** `POST /api/auth/cadastro`.

**Request:** header CSRF e cookies obtidos na etapa anterior, além de
`Content-Type: application/json`.

```json
{
  "nome": "Maria Silva",
  "email": "maria@example.com",
  "senha": "Senha123!"
}
```

| Campo | Tipo | Validação |
| --- | --- | --- |
| `nome` | string | Obrigatório, não pode ser branco; entre 3 e 150 caracteres; após remover espaços externos, deve continuar com pelo menos 3 caracteres |
| `email` | string | Obrigatório, não pode ser branco; formato de email válido; máximo de 254 caracteres; armazenado sem espaços externos e em minúsculas; deve ser único |
| `senha` | string | Obrigatória, não pode ser branca; entre 8 e 72 caracteres e no máximo 72 bytes UTF-8 |

O nome é armazenado sem espaços externos. O usuário é criado como `HUMANO`, com
saldo zero e carteira vinculada. Tipo e saldo não são campos de entrada.
A senha é armazenada como hash BCrypt e não aparece na resposta.

**Response — HTTP 201:**

```json
{
  "cdRetorno": 201,
  "msgTecnica": null,
  "msgUsuario": "Operação realizada com sucesso",
  "dados": {
    "id": 1,
    "nome": "Maria Silva",
    "email": "maria@example.com"
  }
}
```

O cadastro não autentica o usuário automaticamente; fazer login em seguida.

**Possíveis erros:** `400` para validação/JSON inválido; `403` para CSRF inválido;
`409` para email duplicado; `500` para falha inesperada. A validação adicional de
senha pode retornar `Senha deve ter no máximo 72 bytes UTF-8`.

### 3.3. Autenticar usuário

**Objetivo:** validar as credenciais e estabelecer uma sessão autenticada.

**Método e URL:** `POST /api/auth/login`.

**Request:** header CSRF e cookies da mesma sessão, com JSON:

```json
{
  "email": "maria@example.com",
  "senha": "Senha123!"
}
```

Email e senha seguem as restrições declaradas nos DTOs: email obrigatório, válido,
com até 254 caracteres; senha obrigatória entre 8 e 72 caracteres. O email é
normalizado para comparação. A restrição adicional de 72 bytes UTF-8 é verificada
no cadastro.

**Response — HTTP 200:**

```json
{
  "cdRetorno": 200,
  "msgTecnica": null,
  "msgUsuario": "Operação realizada com sucesso",
  "dados": {
    "idSessao": "identificador-da-sessao",
    "usuario": {
      "id": 1,
      "nome": "Maria Silva",
      "email": "maria@example.com"
    }
  }
}
```

O servidor renova o identificador da sessão no login. O cliente deve atualizar e
preservar o cookie `JSESSIONID` recebido. `dados.idSessao` identifica essa sessão,
mas a autenticação das chamadas seguintes é feita pelo cookie, não por um header
Bearer. Para futuros POSTs após o login, obter novamente o CSRF, preservando o
cookie atualizado.

**Possíveis erros:** `400` para validação/JSON inválido; `401` para credenciais
incorretas ou usuário inexistente; `403` para CSRF inválido; `500` para falha inesperada.

### 3.4. Consultar usuário autenticado

**Objetivo:** retornar os dados básicos do usuário da sessão atual.

**Método e URL:** `GET /api/auth/me`.

**Request:** sem corpo; enviar o cookie `JSESSIONID` da sessão autenticada.
Não recebe ID de usuário como parâmetro e não precisa de CSRF.

**Response — HTTP 200:**

```json
{
  "cdRetorno": 200,
  "msgTecnica": null,
  "msgUsuario": "Operação realizada com sucesso",
  "dados": {
    "id": 1,
    "nome": "Maria Silva",
    "email": "maria@example.com"
  }
}
```

**Possíveis erros:** `401` para sessão ausente, inválida, expirada ou usuário não
mais encontrado; `403` em acesso negado; `500` para falha inesperada.

### 3.5. Consultar ativos

**Objetivo:** listar ativos disponíveis e seus preços atuais para exibição ao investidor.

**Método e URL:** `GET /api/ativos`.

**Request:** sem corpo; enviar o cookie `JSESSIONID` da sessão autenticada.
CSRF não é necessário.

| Parâmetro de query | Tipo | Obrigatório | Regra |
| --- | --- | --- | --- |
| `nome` | string | Não | Busca por trecho do nome, ignorando maiúsculas e minúsculas |
| `codigo` | string | Não | Busca por trecho do ticker, ignorando maiúsculas e minúsculas |

Regras da consulta:

- Espaços externos dos filtros são removidos; parâmetros ausentes ou vazios são ignorados.
- Quando ambos os filtros são informados, o ativo precisa satisfazer os dois (`AND`).
- `%` e `_` nos filtros são tratados como texto, não como curingas.
- Sem filtros, todos os ativos cadastrados são retornados.
- A ordenação é crescente por código; não há paginação nesta versão.
- Não há validação de tamanho ou de formato dos filtros na implementação atual.
- `codigo` no JSON corresponde ao campo `ticker` da entidade.
- `precoAtual` é o preço atual persistido, que pode ser diferente do preço inicial.
- A consulta não expõe ID interno, preço inicial ou quantidade total do ativo.

Exemplos:

```http
GET /api/ativos
GET /api/ativos?nome=petro
GET /api/ativos?codigo=PETR4
GET /api/ativos?nome=petro&codigo=4
```

**Response — HTTP 200:**

```json
{
  "cdRetorno": 200,
  "msgTecnica": null,
  "msgUsuario": "Operação realizada com sucesso",
  "dados": [
    {
      "codigo": "PETR4",
      "nome": "Petrobras",
      "precoAtual": 38.50
    }
  ]
}
```

| Campo de cada ativo | Tipo | Descrição |
| --- | --- | --- |
| `codigo` | string | Código de negociação; até 20 caracteres no modelo persistido |
| `nome` | string | Nome do ativo; até 150 caracteres no modelo persistido |
| `precoAtual` | number | Preço atual positivo, persistido com precisão de duas casas decimais |

Se não existirem ativos ou nenhum corresponder aos filtros, o retorno continua
sendo **HTTP 200**, com lista vazia:

```json
{
  "cdRetorno": 200,
  "msgTecnica": null,
  "msgUsuario": "Operação realizada com sucesso",
  "dados": []
}
```

**Possíveis erros:** `401` para sessão ausente/inválida/expirada; `403` em acesso
negado; `500` para falha inesperada. Ausência de resultados não é erro `404`.

### 3.6. Verificar saúde da aplicação

**Objetivo:** verificar a saúde da aplicação, incluindo a disponibilidade do datasource.

**Método e URL:** `GET /actuator/health`.

**Request:** sem corpo, autenticação ou CSRF.

**Response — HTTP 200, aplicação saudável:**

```json
{
  "status": "UP"
}
```

**Possível resposta de indisponibilidade — HTTP 503:**

```json
{
  "status": "DOWN"
}
```

Esse endpoint usa o formato nativo do Actuator, sem envelope `ApiResult` e sem
expor detalhes internos dos componentes (`show-details: never`). Se o processo
estiver parado ou inacessível, a conexão poderá falhar sem resposta HTTP.

## 4. Fluxo de autenticação e consumo

1. Obter `GET /api/auth/csrf` e preservar os cookies da resposta.
2. Se necessário, cadastrar o usuário em `POST /api/auth/cadastro`, enviando o
   token em `X-CSRF-TOKEN` e os cookies preservados.
3. Fazer `POST /api/auth/login` com email, senha, o token e os cookies.
4. Atualizar o cookie `JSESSIONID` a partir da resposta de login.
5. Consultar `GET /api/auth/me` ou `GET /api/ativos`, enviando o cookie atualizado.
6. Para novos POSTs após o login, consultar CSRF novamente usando a sessão atual.

A sessão expira após 30 minutos de inatividade e é mantida na memória da instância;
reiniciar a aplicação encerra as sessões. Cookies usam `HttpOnly`, `SameSite=Strict`
e transporte exclusivo por cookie. Em HTTPS, configurar `SESSION_COOKIE_SECURE=true`.
O frontend deve preservar os cookies; no navegador, chamadas na mesma origem
fazem isso por padrão. Não há configuração de CORS entre origens distintas no
código atual; usar a mesma origem ou um proxy de desenvolvimento.

### 4.1. Exemplo completo com curl e jq

Pré-requisitos: API em execução, `curl` e `jq`. Os comandos usam um arquivo
local de cookies para preservar a sessão e geram um email para o cadastro de exemplo.

```bash
API_URL=http://localhost:8080
API_COOKIES=$(mktemp)
API_EMAIL="investidor-$(date +%s)-${RANDOM}@example.com"

# Obter token e preservar o cookie inicial.
API_CSRF_RESPONSE=$(curl --fail-with-body -sS -c "$API_COOKIES" "$API_URL/api/auth/csrf")
API_CSRF_HEADER=$(echo "$API_CSRF_RESPONSE" | jq -r '.dados.headerName')
API_CSRF_TOKEN=$(echo "$API_CSRF_RESPONSE" | jq -r '.dados.token')

# Cadastrar o investidor; não faz login automaticamente.
curl --fail-with-body -sS -b "$API_COOKIES" -c "$API_COOKIES" \
  -H 'Content-Type: application/json' -H "$API_CSRF_HEADER: $API_CSRF_TOKEN" \
  -d "{\"nome\":\"Investidor Exemplo\",\"email\":\"$API_EMAIL\",\"senha\":\"Senha123!\"}" \
  "$API_URL/api/auth/cadastro" | jq

# Fazer login e salvar o cookie renovado.
curl --fail-with-body -sS -b "$API_COOKIES" -c "$API_COOKIES" \
  -H 'Content-Type: application/json' -H "$API_CSRF_HEADER: $API_CSRF_TOKEN" \
  -d "{\"email\":\"$API_EMAIL\",\"senha\":\"Senha123!\"}" \
  "$API_URL/api/auth/login" | jq

# Consultar o usuário e os ativos; GET não precisa de CSRF.
curl --fail-with-body -sS -b "$API_COOKIES" "$API_URL/api/auth/me" | jq
curl --fail-with-body -sS -b "$API_COOKIES" --get \
  --data-urlencode 'nome=petro' --data-urlencode 'codigo=4' \
  "$API_URL/api/ativos" | jq

# Health check público, com formato próprio.
curl --fail-with-body -sS "$API_URL/actuator/health" | jq
rm "$API_COOKIES"
```

A consulta filtrada pode retornar `dados: []` se os ativos de exemplo não estiverem
cadastrados. No Bruno ou Postman, seguir a mesma sequência com o armazenamento de
cookies habilitado, lendo os resultados dentro de `dados`.

## 5. Referências e rastreabilidade

- [Issue #13 — Documentar especificação inicial da API REST](https://github.com/nataliassantoz/Distributed-Stock-Exchange-Simulator/issues/13).
- Dependências indicadas na issue: BACK-001, AUTH-001 e MARKET-001.
- [Execução e configuração do backend](../../backend/README.md).
- [Coleção Bruno e fluxo de autenticação](../../backend/bruno/README.md).
- [Ativos e perfis de investidores](../01-business/assets-and-investors.md).
- [Regras de mercado](../01-business/market-rules.md).
- [Modelo de dados](../03-database/der.md).
- [AutenticacaoController](../../backend/src/main/java/br/com/nataliasantos/distributedstockexchangesimulator/api/controller/AutenticacaoController.java).
- [AtivosController](../../backend/src/main/java/br/com/nataliasantos/distributedstockexchangesimulator/api/controller/ativos/AtivosController.java).
- [DTOs de usuário e validações](../../backend/src/main/java/br/com/nataliasantos/distributedstockexchangesimulator/api/dto/UsuarioDTO.java).
- [Tratamento global de erros](../../backend/src/main/java/br/com/nataliasantos/distributedstockexchangesimulator/api/exception/GlobalExceptionHandler.java).
- [Configuração de segurança](../../backend/src/main/java/br/com/nataliasantos/distributedstockexchangesimulator/core/configuration/SecurityConfiguration.java).
- [Configuração da aplicação e do Actuator](../../backend/src/main/resources/application.yml).
- Testes de referência: [autenticação e erros](../../backend/src/test/java/br/com/nataliasantos/distributedstockexchangesimulator/AutenticacaoTest.java),
  [resposta de ativos](../../backend/src/test/java/br/com/nataliasantos/distributedstockexchangesimulator/AtivosTest.java),
  [filtros de ativos](../../backend/src/test/java/br/com/nataliasantos/distributedstockexchangesimulator/AtivosBuscaTest.java) e
  [infraestrutura e health check](../../backend/src/test/java/br/com/nataliasantos/distributedstockexchangesimulator/DistributedStockExchangeSimulatorApplicationIT.java).
