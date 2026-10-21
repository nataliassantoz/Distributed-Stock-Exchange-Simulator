
# Ativos e Perfis de Investidores

## 1. Objetivo

Definir os ativos fictícios negociados no mercado e os perfis de investidores que participarão da simulação.

Essas definições servirão de referência para a modelagem das carteiras, o registro de ordens e a implementação dos agentes automatizados.

## 2. Ativos Negociados

A simulação utilizará inicialmente três ações fictícias:

| Código | Empresa fictícia | Descrição | Preço inicial | Quantidade total |
|---|---|---|---:|---:|
| TEC3 | TechNova | Empresa de tecnologia | R$ 50,00 | 1.000 ações |
| ENE3 | EnergiaSul | Empresa do setor de energia | R$ 80,00 | 1.000 ações |
| FIN3 | FinanCorp | Empresa de serviços financeiros | R$ 30,00 | 1.000 ações |

- Cada ativo possui um código único de negociação.
- Os códigos acima representam os ativos inicialmente definidos para a simulação.
- O preço inicial é utilizado até ocorrer a primeira negociação.
- Após a primeira negociação, o preço atual corresponde ao preço da última operação executada.
- A quantidade total de ações de cada ativo permanece constante durante a simulação.
- As negociações apenas transferem ações entre investidores.
- A distribuição inicial das ações é definida na configuração da simulação.
- A soma das ações distribuídas deve corresponder à quantidade total de cada ativo.

## 3. Perfis de Investidores

O sistema contará com investidores humanos e agentes automatizados com diferentes estratégias de negociação.

| Perfil | Objetivo | Estratégia básica | Tolerância ao risco | Frequência de operações |
|---|---|---|---|---|
| Humano | Negociar conforme suas decisões | Escolhe manualmente o ativo, a quantidade e o preço | Definida pelo usuário | Variável |
| Conservador | Preservar o capital | Realiza poucas operações e negocia pequenas quantidades | Baixa | Baixa |
| Moderado | Equilibrar risco e retorno | Negocia quantidades intermediárias e acompanha variações de preço | Média | Média |
| Agressivo | Buscar maiores retornos, aceitando mais risco | Reage frequentemente às variações de preço e negocia quantidades maiores | Alta | Alta |

- Investidores humanos enviam ordens manualmente.
- Agentes automatizados enviam ordens conforme suas estratégias.
- Os perfis conservador, moderado e agressivo representam comportamentos possíveis dos agentes.
- Os critérios exatos de decisão e os intervalos entre operações serão definidos na implementação dos agentes.

## 4. Regras Comuns

- Cada investidor possui um identificador único.
- O capital inicial padrão é de **R$ 10.000,00** por investidor.
- Cada investidor recebe uma quantidade inicial de ações fictícias, definida na configuração da simulação.
- Compras exigem saldo disponível suficiente.
- Vendas exigem ações disponíveis suficientes.
- Não são permitidos saldo negativo, venda a descoberto ou negociação do investidor consigo mesmo.
- Toda ordem deve informar o ativo, a operação (compra ou venda), a quantidade inteira positiva e o preço por ação, maior que zero.
- Todos os investidores, humanos ou automatizados, seguem as mesmas regras de negociação.
- O mercado permanece aberto enquanto a simulação estiver em execução.

## 5. Referências

- [Regras do Mercado](market-rules.md) — regras de negócio aplicadas às negociações.
- [Issue #4 — Definir ativos e perfis de investidores](https://github.com/nataliassantoz/Distributed-Stock-Exchange-Simulator/issues/4).
