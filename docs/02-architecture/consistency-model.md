# Modelo de Consistência dos Componentes

## 1. Objetivo

Definir como o **Distributed Stock Exchange Simulator** deve manter os dados coerentes durante as operações de compra e venda. Este documento registra **decisões de arquitetura**; os mecanismos ainda serão implementados nas etapas futuras.

## 2. Tipos de consistência

- **Forte:** operações sobre dados críticos devem observar um estado coerente e não podem utilizar recursos já reservados ou consumidos.
- **Eventual:** uma informação exibida pode ficar temporariamente desatualizada, desde que seja atualizada posteriormente a partir do estado oficial.

## 3. Estratégia por componente

| Componente | Consistência | Justificativa |
|---|---|---|
| Saldo do investidor | **Forte** | Impedir saldo negativo e uso do mesmo dinheiro em duas ordens. |
| Carteira | **Forte** | Impedir venda de ações inexistentes ou já reservadas. |
| Livro de ofertas | **Forte** | Respeitar a prioridade de preço e tempo e evitar que uma ordem seja executada duas vezes. |
| Histórico de negociações | **Forte no registro; eventual na exibição** | Cada negociação confirmada precisa ser registrada corretamente; a lista mostrada ao usuário pode demorar um pouco para atualizar. |
| Atualizações em tempo real | **Eventual** | O Home Broker pode receber atualizações com pequeno atraso, sem alterar o resultado oficial das negociações. |

## 4. Regras de funcionamento

1. **Reserva de recursos:** antes de aceitar uma ordem, o backend verifica e reserva o saldo ou as ações disponíveis. Recursos reservados não podem ser usados por outra ordem.
2. **Execução de negociações:** ao casar ordens, o sistema deve atualizar de forma conjunta as ordens, os saldos, as carteiras e o registro da negociação. Se alguma etapa falhar, a operação não deve ficar parcialmente confirmada.
3. **Ordem de processamento:** o Livro de Ofertas segue a prioridade de preço e, em caso de empate, a ordem de chegada definida pelo servidor.
4. **Atualização da interface:** após confirmar a operação, o backend poderá enviar notificações por WebSocket. Se a interface estiver desatualizada, uma nova consulta à API deverá recuperar o estado oficial.

**Exemplo:** se um investidor tem R$ 1.000 disponíveis e envia duas compras simultâneas de R$ 800, somente uma poderá reservar esse valor. A outra deverá ser rejeitada por saldo insuficiente.

## 5. Implementação futura e limites

- A arquitetura planejada utiliza **Spring Boot** e **PostgreSQL**. O backend será responsável por coordenar as operações críticas.
- A implementação deverá usar transações de banco de dados para preservar a integridade das negociações e algum mecanismo de controle de concorrência para impedir reservas ou execuções simultâneas conflitantes. A escolha concreta será feita na implementação.
- O **WebSocket** servirá para atualizar a interface, não como fonte oficial dos saldos e das negociações.
- Este documento **não pressupõe** microsserviços separados, replicação de banco, consenso distribuído ou filas de mensagens. Esses recursos não são necessários para cumprir esta issue.
- A consistência eventual da exibição **não autoriza** perda de negociações confirmadas.

## 6. Referências

- [Issue #7 — ARCH-003](https://github.com/nataliassantoz/Distributed-Stock-Exchange-Simulator/issues/7)
- [Regras do mercado](../01-business/market-rules.md)
- [Diagrama de arquitetura](architecture-diagram.md)
- [Decisões tecnológicas](technology-decisions.md)
- [Modelo de dados (DER)](../03-database/der.md)