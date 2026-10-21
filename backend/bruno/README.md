# Collection Bruno — Autenticação

1. Inicie a API conforme o [README do backend](../README.md).
2. No Bruno, use **Open Collection** e selecione esta pasta (`backend/bruno`).
3. Selecione o ambiente **Local**. Ajuste `baseUrl` se a API usar outra porta.
4. Mantenha o armazenamento e o envio automático de cookies habilitados.
5. Execute a collection completa, na ordem de 01 a 07, pelo Collection Runner.

O primeiro request gera um email aleatório para permitir novas execuções sem
conflito com cadastros anteriores e salva o token CSRF. O cadastro salva `usuarioId`
e o login salva `idSessao` como variáveis de execução. O Bruno recebe o cookie
`JSESSIONID` automaticamente e o envia na consulta `/me`. O identificador retornado
no JSON não precisa ser copiado para um header.

Para executar manualmente, siga a mesma ordem. Para consultar outro usuário já
cadastrado, ajuste o body de Login e obtenha primeiro o CSRF. Os testes de Login e
`/me` comparam com o usuário criado nesta execução.

As três últimas requisições validam rejeições esperadas: email duplicado (409),
cadastro inválido (400) e senha incorreta (401). São consideradas aprovadas quando
recebem esses códigos. Cada execução completa persiste um novo usuário e carteira
no banco configurado da API; use um banco de desenvolvimento/testes.

Com a CLI do Bruno instalada, execute nesta pasta:

```bash
bru run --env Local
```

Os scripts usam as [APIs de scripting do Bruno](https://github.com/usebruno/bruno-docs/blob/main/testing/script/javascript-reference.mdx).
