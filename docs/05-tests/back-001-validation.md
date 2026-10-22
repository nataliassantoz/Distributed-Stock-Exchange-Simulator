# BACK-001 — validação da fundação

Validação local realizada em 21/10/2026 com Java 21 e PostgreSQL 17.

- `./mvnw clean verify`: BUILD SUCCESS, 2 testes executados sem falhas.
- Aplicação iniciada em porta HTTP aleatória pelo teste de integração.
- Conexão PostgreSQL validada com `SELECT 1`.
- Bean Flyway carregado, tabela `public.flyway_schema_history` encontrada e nenhuma migration pendente.
- Requisição HTTP real para `/actuator/health`: HTTP 200, campo `status` igual a `UP`.
- `docker compose --profile app config --quiet`: configuração válida.
- `docker compose --profile app build backend`: imagem construída com sucesso.
- `APP_PORT=18080 docker compose --profile app up -d --no-build --wait`: API iniciada em contêiner.
- `curl --fail http://localhost:18080/actuator/health`: HTTP 200,
  `{"groups":["liveness","readiness"],"status":"UP"}`.
- Consulta ao histórico via `psql`: tabela existente, zero migrations (esperado nesta etapa).

A porta 18080 foi usada porque a 8080 já estava ocupada. O serviço existente foi preservado.

Não há entidades ou migrations de negócio nesta etapa. O Flyway inicializa somente
seu histórico. O teste exige PostgreSQL disponível e as variáveis exportadas conforme
o README do backend.

A captura visual do endpoint solicitada pela issue deve ser anexada após abrir
`http://localhost:18080/actuator/health` (ou 8080 na configuração padrão) no navegador. Os testes HTTP acima são evidência
automatizada; este documento não representa uma captura visual.

## Correção do build padrão

Os testes foram movidos para `DistributedStockExchangeSimulatorApplicationIT`,
executados pelo Failsafe no perfil Maven `integration`.
`./mvnw clean install` não depende do banco; `./mvnw clean verify -Pintegration`
executa os dois testes com PostgreSQL disponível e credenciais exportadas.
Os comandos `clean verify` registrados acima correspondem à validação anterior
à separação dos testes de integração.

Validação após a correção:
- `./mvnw clean install`, sem variáveis DB/POSTGRES_PASSWORD: BUILD SUCCESS.
- `./mvnw clean verify -Pintegration`, com credenciais exportadas: BUILD SUCCESS,
  2 testes sem falhas ou erros.
