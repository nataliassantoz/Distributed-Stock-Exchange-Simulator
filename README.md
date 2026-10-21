
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
| Semana 1 — Planejamento e Arquitetura | ✅ Concluído |
| Semana 2 — Arquitetura e Modelagem de Dados | ✅ Concluído  |
| Semana 3 — Fundação do Backend | ✅ Concluído |
| Semana 4 — Conexão Múltipla e Ordens Básicas | 🟡 Em desenvolvimento |
| Semana 5 — Home Broker Simples | 🟡 Em desenvolvimento |
| Semana 6 — Consolidação da Primeira Entrega | 🟡 Em desenvolvimento |
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
| R1 | Múltiplos clientes | Pelo menos dois investidores devem poder operar simultaneamente. | M4 | #15, #16 | Session, SessionService, SessionController | #18 — TEST-001 | ⬜ |
| R2 | Autenticação | Cada investidor deve possuir uma sessão identificável. | M3 | #11 | AuthController, AuthService, Session | — | ⬜ |
| R3 | Home Broker | Investidor deve interagir através de uma interface web. | M5 | #19 | Home Broker Frontend | #23 — TEST-002 | ⬜ |
| R4 | Consulta de ativos | Investidor deve poder consultar os ativos negociáveis e seus preços. | M3/M5 | #12, #14 | AssetController, AssetService | #23 — TEST-002 | ⬜ |
| R5 | Envio de ordens | Investidores devem registrar ordens de compra e venda. | M4/M5 | #17, #21 | OrderController, OrderService, Order | #18, #23 | ⬜ |
| R6 | Livro de Ofertas | Sistema deve organizar ordens pendentes por ativo, mantendo separadas as ofertas de compra e venda. | M7 | #29, #30, #31 | OrderBook, OrderBookService | #32 — TEST-003 | ⬜ |
| R7 | Matching Engine | Sistema deve identificar e executar automaticamente ordens de compra e venda compatíveis. | M8 | #33, #34, #35, #36, #37 | MatchingEngine, TradeService, Trade, WalletService, PortfolioService | #38 — TEST-004 | ⬜ |
| R8 | Atualização em tempo real | Clientes conectados devem receber automaticamente as alterações do mercado sem recarregar a interface. | M9 | #41, #42 | NotificationService, mecanismo de atualização em tempo real | #42 — TEST-005 | ⬜ |
| R9 | Persistência | Dados relevantes do sistema devem permanecer disponíveis após a reinicialização da aplicação. | M9 | #39, #40 | Repositories, entidades persistentes, banco de dados | Testes de persistência | ⬜ |
| R10 | Eventos simulados | O sistema deve suportar agentes automatizados e eventos econômicos capazes de alterar o comportamento do mercado. | M10 | #43, #44, #45 | Agentes automatizados, estratégias e mecanismo de eventos | Cenários de agentes/eventos | ⬜ |
| R11 | Recuperação de falhas | O sistema deve detectar desconexões, permitir reconexão e recuperar o estado do investidor. | M10/M11 | #46, #47, #52 | Session/Recovery, mecanismos de reconexão e recuperação | #52 — FAULT-002 | ⬜ |
| R12 | Testes de carga | Sistema deve ser validado sob múltiplos usuários/agentes simultâneos. | M11 | #50, #51 | Suíte de testes funcionais, concorrência e carga | #50, #51 | ⬜ |
| R13 | Observabilidade | Operações do sistema devem ser rastreáveis através de logs estruturados por ordem. | M11 | #49 | Logging/Correlation | Validação de logs | ⬜ |
| R14 | Segurança | Usuários não devem acessar ou alterar recursos pertencentes a outros investidores. | M11 | #48 | Validações de autorização | Validação de segurança | ⬜ |
| R15 | Contêineres | Os componentes necessários à execução do sistema devem ser disponibilizados em contêineres. | M3 | #9 | Dockerfile, Docker Compose, configuração de containers | Teste de inicialização dos containers | ⬜ |

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
