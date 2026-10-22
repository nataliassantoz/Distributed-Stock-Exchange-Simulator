# Sessões ativas — SESSION-001

Abra `backend/bruno` como collection e selecione o ambiente **Local**.
Inicie a API e mantenha o armazenamento/envio automático de cookies habilitado.
Execute esta pasta na ordem de 01 a 09 pelo Runner.

O fluxo obtém CSRF, cadastra um investidor com email aleatório, faz login,
consulta a sessão atual, lista as sessões do próprio usuário, obtém CSRF para
logout e encerra a sessão. As duas últimas consultas esperam **401**, confirmando
que o cliente perdeu acesso após o logout.

As variáveis de execução usam o prefixo `session` para não sobrescrever as da
pasta `autenticacao`. `baseUrl`, `nome` e `senha` vêm do ambiente **Local**.
Cada execução cria um usuário no banco: utilize um ambiente de desenvolvimento.
Para repetir o fluxo, comece novamente pela requisição 01.

Com a CLI do Bruno instalada, execute a partir de `backend/bruno`:

```bash
bru run session --env Local
```

Para validar dois investidores simultâneos, use dois clientes com cookies
separados, execute 01 a 05 em ambos e depois 06 a 09 somente no primeiro.
No segundo cliente, repita 04 e 05: as respostas devem continuar sendo 200.
