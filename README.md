
# Distributed Stock Exchange Simulator

Simulador de uma Bolsa de Valores Distribuída desenvolvido como projeto acadêmico da disciplina de **Sistemas Distribuídos**.

O projeto tem como objetivo simular o funcionamento de uma plataforma de negociação de ativos, permitindo a conexão de múltiplos investidores, envio e processamento de ordens, formação de negociações, atualização do mercado em tempo real, persistência de dados e tratamento de falhas.

---

# 📌 Sobre o Projeto

O **Distributed Stock Exchange Simulator** representa uma bolsa de valores simplificada, na qual múltiplos clientes podem se conectar simultaneamente para consultar o mercado e realizar operações de compra e venda de ativos.

Ao longo do desenvolvimento serão implementados os principais componentes necessários para a simulação de um ambiente distribuído de negociação, incluindo:

- Investidores e ativos
- Saldo e carteira
- Ordens de compra e venda
- Livro de ofertas (Order Book)
- Matching Engine
- Execução de negociações
- Persistência dos dados
- Atualização em tempo real
- Múltiplos clientes simultâneos
- Agentes automatizados
- Eventos econômicos simulados
- Tolerância a falhas e recuperação
- Segurança e autorização
- Observabilidade
- Testes funcionais, concorrência e carga

---

## 🚀 Executando o Projeto

Consulte o guia completo:

👉 [Como Executar o Projeto](docs/00-getting-started/setup-and-run.md)

---

# 🧭 Como Navegar no Projeto

Se esta é sua primeira visita ao repositório, siga a ordem abaixo:

### 1. Entenda o planejamento

Consulte os **Milestones** para visualizar a evolução planejada do projeto.

👉 https://github.com/nataliassantoz/Distributed-Stock-Exchange-Simulator/milestones

### 2. Consulte as atividades

As **Issues** descrevem detalhadamente cada tarefa necessária para concluir os milestones.

👉 https://github.com/nataliassantoz/Distributed-Stock-Exchange-Simulator/issues

### 3. Acompanhe o desenvolvimento

O **GitHub Project Board** mostra quais atividades estão pendentes, em andamento, em revisão ou concluídas.

👉 https://github.com/users/nataliassantoz/projects/2/views/1?layout_template=board

### 4. Explore a implementação

O código-fonte está organizado principalmente nas pastas:

```text
backend/
frontend/
tests/
```

### 5. Consulte a documentação

Toda a documentação técnica e gerencial está organizada na pasta:

```text
docs/
```

---

# 📊 Organização do Projeto

O projeto utiliza os recursos do GitHub para garantir rastreabilidade entre planejamento, implementação e validação.

```text
Projeto
│
├── 📅 Milestones
│   └── Planejamento das entregas
│
├── 📋 Issues
│   └── Atividades detalhadas
│
├── 📊 Project Board
│   └── Fluxo de execução
│
├── 💻 Código-fonte
│   ├── Backend
│   └── Frontend
│
├── 🧪 Testes
│
└── 📚 Documentação
```

---

# 📅 Roadmap do Projeto

As entregas estão organizadas em milestones semanais.

| Semana | Milestone | Objetivo |
|----------|----------|----------|
| 1 | Planejamento e Arquitetura | Definição do problema, requisitos e arquitetura inicial |
| 2 | Arquitetura e Modelagem de Dados | Modelagem arquitetural e banco de dados |
| 3 | Fundação do Backend | Estrutura inicial do servidor |
| 4 | Conexão Múltipla e Ordens Básicas | Comunicação entre clientes e envio de ordens |
| 5 | Home Broker Simples | Interface inicial do investidor |
| 6 | Consolidação da Primeira Entrega | Revisão e validação da entrega parcial |
| 7 | Livro de Ofertas | Implementação do Order Book |
| 8 | Matching Engine e Negociações | Processamento automático das negociações |
| 9 | Persistência e Tempo Real | Persistência dos dados e atualização dos clientes |
| 10 | Tolerância a Falhas e Recuperação | Reconexão e recuperação de estado |
| 11 | Segurança, Observabilidade e Testes | Segurança, logs, concorrência e carga |
| 12 | Consolidação e Entrega Final | Revisão, documentação e entrega final |

---

# 📋 Issues

Todas as atividades do projeto são rastreadas através das **Issues do GitHub**.

Cada issue possui:

- Objetivo
- Contexto
- Escopo
- Critérios de aceite
- Estratégia de teste
- Artefatos esperados
- Evidências de conclusão

---

# 📊 Project Board

O gerenciamento operacional do projeto é realizado através do GitHub Project.

👉 https://github.com/users/nataliassantoz/projects/2/views/1?layout_template=board

Fluxo de trabalho:

```text
Tasks
  ↓
Ready to Start
  ↓
Implementation
  ↓
Review
  ↓
Done
```

<img width="1915" height="442" alt="image" src="https://github.com/user-attachments/assets/a41277c9-024d-43e9-915f-0f7820199b48" />


## Significado das colunas

| Coluna | Descrição |
|----------|----------|
| Tasks | Atividades planejadas |
| Ready to Start | Atividades prontas para desenvolvimento |
| Implementation | Atividades em execução |
| Review | Atividades aguardando validação |
| Done | Atividades concluídas |

O Board representa o andamento diário do projeto, enquanto os Milestones representam o planejamento de alto nível.

---

# 📚 Estrutura da Documentação

A documentação está organizada por domínio.

```text
docs/
│
├── 01-business/
│   ├── market-rules.md
│   └── assets-and-investors.md
│
├── 02-architecture/
│   ├── decisions.md
│   ├── architecture-diagram.md
│   ├── consistency-model.md
│   └── fault-tolerance.md
│
├── 03-database/
│   └── der.md
│
├── 04-api/
│   └── api-specification.md
│
├── 05-tests/
│
├── 06-proposal/
│   └── project-proposal.md
│
├── 07-project-management/
│   └── updated-roadmap.md
│
├── 08-presentation/
│   └── demo-script.md
│
├── 09-delivery/
│   └── delivery-checklist.md
│
└── 10-trading/
    ├── order-book.md
    └── matching-engine.md
```

## Principais áreas

### 01-business

Regras de negócio da bolsa de valores simulada.

### 02-architecture

Arquitetura distribuída, consistência, decisões arquiteturais e tolerância a falhas.

### 03-database

Modelagem do banco de dados e DER.

### 04-api

Especificação dos endpoints da aplicação.

### 05-tests

Cenários, evidências e validações realizadas.

### 06-proposal

Proposta oficial do projeto.

### 07-project-management

Planejamento e roadmap.

### 08-presentation

Materiais utilizados na apresentação.

### 09-delivery

Artefatos relacionados à entrega da disciplina.

---

# 🔄 Fluxo de Desenvolvimento

Cada funcionalidade segue o seguinte ciclo:

```text
Requisito
    ↓
Milestone
    ↓
Issue
    ↓
Implementação
    ↓
Review
    ↓
Teste
    ↓
Evidência
    ↓
Conclusão
```

Esse fluxo garante rastreabilidade completa entre os requisitos do projeto e os artefatos produzidos.

---

# 📈 Status do Projeto

| Milestone | Status |
|------------|----------|
| Semana 1 — Planejamento e Arquitetura | 🟡 Em andamento |
| Semana 2 — Arquitetura e Modelagem de Dados | ⚪ Não iniciada |
| Semana 3 — Fundação do Backend | ⚪ Não iniciada |
| Semana 4 — Conexão Múltipla e Ordens Básicas | ⚪ Não iniciada |
| Semana 5 — Home Broker Simples | ⚪ Não iniciada |
| Semana 6 — Consolidação da Primeira Entrega | ⚪ Não iniciada |
| Semana 7 — Livro de Ofertas | ⚪ Não iniciada |
| Semana 8 — Matching Engine e Negociações | ⚪ Não iniciada |
| Semana 9 — Persistência e Tempo Real | ⚪ Não iniciada |
| Semana 10 — Tolerância a Falhas e Recuperação | ⚪ Não iniciada |
| Semana 11 — Segurança, Observabilidade e Testes | ⚪ Não iniciada |
| Semana 12 — Consolidação e Entrega Final | ⚪ Não iniciada |

> O acompanhamento detalhado deve ser realizado através dos Milestones e do Project Board.

---

# 🔗 Links Importantes

| Recurso | Link |
|----------|----------|
| Repositório | https://github.com/nataliassantoz/Distributed-Stock-Exchange-Simulator |
| Milestones | https://github.com/nataliassantoz/Distributed-Stock-Exchange-Simulator/milestones |
| Issues | https://github.com/nataliassantoz/Distributed-Stock-Exchange-Simulator/issues |
| Project Board | https://github.com/users/nataliassantoz/projects/2/views/1?layout_template=board |

---

# 🧩 Conceitos de Sistemas Distribuídos

O projeto explora diversos conceitos clássicos da área de Sistemas Distribuídos:

- Comunicação entre processos
- Múltiplos clientes simultâneos
- Concorrência
- Sincronização
- Consistência de estado
- Persistência
- Comunicação em tempo real
- Tolerância a falhas
- Recuperação de estado
- Observabilidade
- Segurança
- Testes de concorrência
- Testes de carga

---

# 🎯 Objetivo Final

Ao final do desenvolvimento, o sistema deverá ser capaz de representar o fluxo completo de negociação de uma bolsa de valores distribuída:

```text
Investidor
    ↓
Home Broker
    ↓
API
    ↓
Order Book
    ↓
Matching Engine
    ↓
Negociação
    ↓
Atualização de Saldo e Carteira
    ↓
Persistência
    ↓
Atualização em Tempo Real
    ↓
Múltiplos Clientes
```

Todo o projeto é desenvolvido de forma incremental utilizando **Milestones, Issues, Project Board, documentação e testes**, garantindo rastreabilidade completa entre planejamento, implementação e validação.

# 📋 Matriz de Rastreabilidade dos Requisitos

Esta matriz estabelece a relação entre os requisitos do sistema, os milestones do projeto, as issues responsáveis pela implementação, os artefatos produzidos e os testes de validação.

| ID | Requisito | Descrição | Milestone | Issue(s) | Artefato(s) | Teste(s) | Status |
|----|-----------|------------|------------|------------|------------|------------|------------|
| R1 | Múltiplos clientes | Pelo menos dois investidores devem poder operar simultaneamente | M4 | # | MultiClientManager | multi-client-test.md | ⬜ |
| R2 | Autenticação | Cada investidor deve possuir uma sessão identificável | M3 | # | AuthService | authentication-test.md | ⬜ |
| R3 | Envio de ordens | Investidores devem registrar ordens de compra e venda | M4 | # | OrderController | order-flow-test.md | ⬜ |
| R4 | Home Broker | Investidor deve interagir através de uma interface web | M5 | # | Home Broker Frontend | end-to-end-homebroker.md | ⬜ |
| R5 | Consulta de saldo e carteira | Investidor deve visualizar saldo disponível e ativos em carteira | M5 | # | PortfolioService | end-to-end-homebroker.md | ⬜ |
| R6 | Livro de Ofertas | Sistema deve organizar ordens pendentes por ativo | M7 | # | OrderBook | order-book-validation.md | ⬜ |
| R7 | Matching Engine | Sistema deve executar negociações automaticamente | M8 | # | MatchingEngine | matching-engine-validation.md | ⬜ |
| R8 | Persistência | Dados devem permanecer disponíveis após reinicialização do sistema | M9 | # | Repository Layer | persistence-test.md | ⬜ |
| R9 | Atualização em tempo real | Clientes devem receber atualizações do mercado em tempo real | M9 | # | NotificationService | synchronization-test.md | ⬜ |
| R10 | Eventos simulados | Eventos econômicos devem impactar o comportamento do mercado | M10 | # | MarketEventEngine | event-simulation-test.md | ⬜ |
| R11 | Recuperação de falhas | Sistema deve recuperar estado após falhas e reconexões | M10 | # | RecoveryService | recovery-test.md | ⬜ |
| R12 | Segurança | Usuários não devem acessar recursos de outros investidores | M11 | # | SecurityConfig | security-validation.md | ⬜ |
| R13 | Observabilidade | Operações devem ser registradas através de logs e métricas | M11 | # | LoggingService | observability-test.md | ⬜ |
| R14 | Testes de carga | Sistema deve ser validado sob múltiplos usuários simultâneos | M11 | # | Load Test Suite | load-test-results.md | ⬜ |

---

## 📊 Legenda de Status

| Status | Significado |
|----------|----------|
| ⬜ | Não iniciado |
| 🟡 | Em desenvolvimento |
| 🔵 | Em validação |
| ✅ | Concluído |
| ❌ | Não atendido |

---

## 🔄 Fluxo de Rastreabilidade

Cada requisito deve possuir rastreabilidade completa ao longo do projeto.

```text
Requisito
    ↓
Milestone
    ↓
Issue
    ↓
Implementação
    ↓
Teste
    ↓
Evidência
    ↓
Conclusão
```

### Exemplo

```text
R6 — Livro de Ofertas
    ↓
Milestone 7
    ↓
Issues #31 #32 #33 #34 #35
    ↓
OrderBook
    ↓
order-book.md
    ↓
order-book-validation.md
    ↓
Conclusão
```
