# Investidores conectados — SESSION-002

Abra `backend/bruno` no Bruno e selecione o ambiente **Local**.
Mantenha o armazenamento e o envio automático de cookies habilitados.

Faça login pela pasta `autenticacao` antes de executar esta pasta. As requisições
reutilizam o cookie `JSESSIONID` recebido no login; não é necessário copiar o
identificador de sessão nem enviar CSRF nas consultas GET.

Execute na ordem:

1. **Consultar sessao atual**: confirma a autenticação e salva o identificador do
   usuário em `investidoresUsuarioId`, sem depender das variáveis de outras pastas.
2. **Listar investidores conectados**: consulta `GET /mercado/investidores` e valida
   a presença do usuário atual, os campos da resposta, a ordenação e a ausência de
   investidores duplicados e dados privados.

A pasta `session` também pode consultar a sessão utilizada aqui. Se executar seu
logout, faça login novamente antes de consultar investidores; sem sessão válida,
a API retorna `401`.

Para validar múltiplos participantes, autentique outro usuário em um cliente com
cookies separados e repita a listagem: ambos devem aparecer. Faça logout do outro
cliente e consulte novamente: ele deve desaparecer se não possuir outra sessão
válida. Duas sessões do mesmo investidor devem resultar em um único item, com
`quantidadeSessoes` igual a 2.
