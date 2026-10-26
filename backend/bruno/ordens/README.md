# Registro de ordens — ORDER-001

Abra `backend/bruno` no Bruno, selecione **Local** e mantenha os cookies
habilitados. Inicie a API conforme o [README do backend](../../README.md).

Antes de executar, configure `ordemAtivoId` no ambiente **Local** com o ID de um
ativo existente (valor inicial: `1`). A listagem pública de ativos não retorna IDs;
consulte o banco de desenvolvimento:

```sql
SELECT id, ticker, nome FROM ativo ORDER BY id;
```

Se o banco estiver vazio, crie um ativo de teste:

```sql
INSERT INTO ativo (ticker, nome, preco_inicial, preco_atual, quantidade_total)
VALUES ('ORD17', 'Ativo de teste', 20.00, 20.00, 1000);
```

Consulte novamente o ID e configure `ordemAtivoId`. O caso de ativo inexistente
usa `2147483647`: esse ID não deve existir no banco de teste.

Execute somente esta pasta, na ordem de 01 a 16. Não é necessário fazer login
antes: o fluxo cadastra um investidor com email aleatório e autentica esse usuário.
Ele renova o CSRF após o login e salva `ordemCompraId` e `ordemVendaId` para consulta.
As demais variáveis de execução usam o prefixo `ordens`.

| Requisições | Resultado esperado |
| --- | --- |
| 01–04: CSRF, cadastro, login e novo CSRF | Autenticação e preparação |
| 05–06: compra e venda | 201, IDs distintos, investidor da sessão, status PENDENTE |
| 07–10: quantidade zero/fracionária e preço negativo/com três casas | 400, mensagem de validação por campo |
| 11: tipo inválido | 400 |
| 12: ativo inexistente | 404 |
| 13: ordem sem CSRF | 403 |
| 14–15: logout e CSRF anônimo | 200 |
| 16: ordem sem autenticação, com CSRF válido | 401 |

Cada execução cria um usuário, uma carteira e duas ordens no banco. Use um banco
de desenvolvimento/testes. O fluxo termina sem sessão autenticada. Para repetir,
comece pela requisição 01. Os erros esperados contam como testes aprovados.

Com a CLI do Bruno instalada, execute de `backend/bruno`:

```bash
bru run ordens --env Local
```

Para comprovar persistência, substitua os IDs abaixo pelos salvos nas variáveis:

```sql
SELECT id, usuario_id, ativo_id, tipo, quantidade_original, quantidade_restante,
       preco_limite, status, criada_em
FROM ordem
WHERE id IN (<ordemCompraId>, <ordemVendaId>);
```

As duas ordens devem estar `PENDENTE`, com quantidades original e restante iguais
a 10. Os testes HTTP verificam as respostas; a consulta SQL confirma a persistência.
Nesta etapa não há reservas, matching ou alteração de saldo/carteira.
