# Estratégia de Tolerância a Falhas

## 1. Objetivo

Definir o que o **Distributed Stock Exchange Simulator** deve fazer quando um investidor perde a conexão, volta a se conectar ou quando o servidor reinicia.

**Por que precisamos disso?** Imagine que um investidor enviou uma ordem de compra e ficou sem internet. A ordem pode continuar sendo negociada no servidor. Quando ele voltar, a tela precisa mostrar o que realmente aconteceu, sem perder ou duplicar operações.

Este documento é um **guia para a implementação futura**. Os comportamentos descritos são planejados; não significam que já foram programados.

## 2. Como o sistema vai lidar com falhas

A estratégia utiliza os componentes já previstos na arquitetura:

| Componente | Papel na recuperação |
|---|---|
| **PostgreSQL** | Guarda os dados confirmados: saldos, carteiras, ordens e negociações. É a fonte oficial das informações. |
| **API REST (Spring Boot)** | Permite consultar novamente o saldo, a carteira e as ordens após uma desconexão. |
| **WebSocket** | Envia atualizações em tempo real enquanto o cliente está conectado. Mensagens perdidas não substituem os dados do banco. |
| **Transações do banco** | Impedem que uma negociação fique parcialmente confirmada quando ocorre um erro. |

**Distinção importante:** *reconectar* significa restabelecer a comunicação; *recuperar o estado* significa consultar e restaurar as informações corretas. **Rollback** é diferente: serve para desfazer alterações de uma transação que não foi concluída.

## 3. Cenários de falha

### 3.1. O investidor perde a internet

**Exemplo:** João tem R$ 2.000 e envia uma ordem para comprar **10 ações a R$ 100 cada**. O servidor aceita a ordem e reserva **R$ 1.000**. Em seguida, a internet de João cai.

**Comportamento esperado:**

1. O Vue identifica que perdeu a comunicação e informa ao usuário que está desconectado.
2. A ordem **continua válida**, porque já foi aceita pelo servidor.
3. O saldo reservado permanece comprometido com a ordem; ele não fica disponível para outras compras.
4. Enquanto o servidor continuar funcionando, a ordem pode ser executada mesmo com João offline.
5. Uma ordem enviada sem confirmação **não deve ser tratada como aceita apenas porque o usuário clicou no botão**.

**Regra principal:** perder a conexão **não cancela** uma ordem já registrada.

### 3.2. O investidor volta a se conectar

**Continuando o exemplo:** enquanto João estava offline, **4 das 10 ações foram compradas por R$ 100 cada**. Agora restam **6 ações pendentes**, com **R$ 600 reservados** para essa parte da ordem.

A tela antiga de João pode ainda mostrar 10 ações pendentes. Por isso, reconectar o WebSocket não é suficiente.

**Fluxo de recuperação:**

1. O Vue restabelece a comunicação com o backend.
2. O backend verifica a identificação/sessão do investidor. Se a sessão não for mais válida, será necessário autenticar novamente.
3. O Vue consulta a API REST para buscar **saldo, carteira e ordens atualizados**.
4. O Spring Boot consulta os dados confirmados no PostgreSQL.
5. O Vue substitui as informações antigas pelas atuais: **4 ações executadas e 6 pendentes**.
6. O cliente volta a acompanhar novas atualizações pelo WebSocket.

**Regra principal:** depois de reconectar, a interface deve confiar nos dados oficiais do backend, e não apenas no que estava exibindo antes da queda.

> Os caminhos específicos dos endpoints REST e o mecanismo de autenticação serão definidos na implementação.

### 3.3. O servidor Spring Boot reinicia

**Exemplo:** existem ordens pendentes no Livro de Ofertas e o processo Java é encerrado inesperadamente.

Se o Livro de Ofertas estiver na memória, seu conteúdo será perdido quando o processo parar. Entretanto, as ordens já confirmadas devem continuar registradas no PostgreSQL.

**Fluxo de recuperação:**

1. Enquanto o backend estiver indisponível, ele não recebe nem executa novas ordens.
2. Quando o Spring Boot iniciar novamente, consulta no banco as ordens ainda pendentes.
3. Reconstrói o Livro de Ofertas com **preço, quantidade restante e ordem de chegada** registrados.
4. Respeita a prioridade de **preço-tempo** prevista nas regras do mercado.
5. Recupera as reservas já existentes, **sem reservar o mesmo dinheiro ou as mesmas ações novamente**.
6. Somente depois de recuperar um estado coerente o processamento das ordens é retomado.

**Exemplo prático:** se uma ordem originalmente tinha 10 ações e 4 já foram executadas, o servidor deve recuperar **somente as 6 restantes**. Não pode recriar a ordem como se nenhuma negociação tivesse acontecido.

**Regra principal:** o reinício do servidor não pode apagar negociações confirmadas nem duplicar ordens ou reservas.

### 3.4. Uma ordem está pendente durante uma falha

| Situação | O que deve acontecer |
|---|---|
| O investidor desconecta | A ordem continua pendente e pode ser executada pelo servidor. |
| O servidor reinicia | A ordem é reconstruída a partir do banco. |
| A ordem foi parcialmente executada | Somente a quantidade restante continua pendente. |
| O investidor solicita cancelamento válido | A parte ainda pendente é cancelada e a reserva correspondente é liberada. |

**Exemplo:** Maria envia uma ordem para comprar 10 ações. Cinco são executadas antes de uma falha. Após a recuperação, a ordem deve continuar com **5 ações pendentes**, não com 10.

## 4. Cuidados importantes ao programar

### 4.1. Evitar ordens duplicadas

**Problema:** Ana clica em **Comprar**. O servidor registra a ordem, mas a conexão cai antes de enviar a resposta. Ana não sabe se a compra foi registrada e tenta novamente.

**Risco:** criar duas ordens quando a intenção era criar apenas uma.

**Decisão:** o cliente não deve reenviar automaticamente uma solicitação cujo resultado é desconhecido. Antes de tentar novamente, deve verificar se a ordem já existe. O mecanismo concreto para identificar tentativas repetidas será definido durante a implementação.

### 4.2. Evitar negociações pela metade

**Problema:** o backend inicia uma negociação, atualiza o dinheiro do comprador e falha antes de atualizar a carteira e o vendedor.

**Risco:** os dados financeiros ficam inconsistentes.

**Decisão:** as alterações relacionadas a uma negociação devem ser feitas em uma **transação de banco de dados**. Se ela falhar antes de ser confirmada, as alterações da transação devem ser desfeitas (**rollback**).

**Atenção:** rollback não é o mecanismo de reconexão. Ele protege a integridade das operações; a recuperação de estado consulta os dados confirmados.

### 4.3. Não duplicar reservas

**Problema:** uma ordem pendente já possui R$ 600 reservados. Após reiniciar, o backend reserva mais R$ 600 para a mesma ordem.

**Risco:** o investidor passa a ter dinheiro bloqueado indevidamente.

**Decisão:** recuperar a reserva persistida, sem aplicar uma segunda reserva.

### 4.4. Não confiar somente no WebSocket

**Problema:** o cliente perde mensagens enquanto está offline.

**Decisão:** após reconectar, consultar o estado oficial via REST. O WebSocket serve para acompanhar as próximas mudanças, não para reconstruir sozinho tudo o que aconteceu.

## 5. O que precisará ser implementado futuramente

| Parte do sistema | Responsabilidade planejada |
|---|---|
| **Vue** | Informar desconexão, restabelecer comunicação e atualizar a tela com consultas REST. |
| **Controllers / API REST** | Expor consultas autenticadas de saldo, carteira e ordens. |
| **Services** | Coordenar consultas e operações, respeitando regras de negócio e consistência. |
| **Persistência / PostgreSQL** | Armazenar ordens, reservas e negociações confirmadas. |
| **Inicialização do backend / Livro de Ofertas** | Recuperar ordens pendentes e reconstruir sua prioridade após reinício. |
| **Transações** | Impedir negociações parcialmente confirmadas. |
| **Controle de duplicidade** | Evitar repetir ordens quando uma resposta se perder durante o envio. |

Esta tabela representa **responsabilidades**, não classes, endpoints ou funcionalidades já existentes.

## 6. Como verificar se a recuperação funciona

Os testes abaixo deverão ser realizados depois que os mecanismos forem implementados:

- [ ] **Desconexão:** uma ordem aceita continua registrada quando o investidor perde a internet.
- [ ] **Execução durante desconexão:** ao reconectar, o investidor vê a quantidade realmente executada e a quantidade restante.
- [ ] **Sessão:** a recuperação só mostra dados do investidor devidamente identificado.
- [ ] **Reinício:** ordens pendentes reaparecem no Livro de Ofertas sem duplicação e com a prioridade correta.
- [ ] **Reservas:** dinheiro e ações não são reservados duas vezes após o reinício.
- [ ] **Transação interrompida:** uma falha não deixa dinheiro, carteira e negociação parcialmente atualizados.
- [ ] **Envio sem resposta:** uma conexão instável não provoca reenvio automático da mesma ordem.

## 7. Limites da estratégia

Para manter o projeto viável para desenvolvimento individual, **não serão previstos nesta etapa** servidores redundantes, troca automática de servidor, filas externas ou processamento de ordens enquanto o backend estiver desligado.

O foco é **preservar os dados confirmados e retomar o funcionamento corretamente após uma falha**.

O método específico de autenticação, o mecanismo de prevenção de duplicidades e os detalhes de concorrência serão escolhidos na implementação. A recuperação depende de persistência correta no PostgreSQL.

## 8. Resumo para consulta rápida

- **Cliente desconectou?** A ordem aceita continua válida.
- **Cliente reconectou?** Valide a sessão, consulte o banco via API e atualize a interface.
- **Servidor reiniciou?** Recupere as ordens pendentes e reconstrua o Livro de Ofertas.
- **Ordem foi parcialmente executada?** Recupere somente a quantidade restante.
- **Negociação falhou no meio?** A transação deve impedir alterações parcialmente confirmadas.
- **Envio de ordem ficou sem resposta?** Não reenvie automaticamente sem verificar o resultado.

## 9. Referências

- [Issue #8 — DS-001: Tolerância a falhas](https://github.com/nataliassantoz/Distributed-Stock-Exchange-Simulator/issues/8)
- [Diagrama de arquitetura](architecture-diagram.md)
- [Modelo de consistência](consistency-model.md)
- [Regras do mercado](../01-business/market-rules.md)
- [Modelo de dados (DER)](../03-database/der.md)