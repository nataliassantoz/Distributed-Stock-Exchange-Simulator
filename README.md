


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

As issues representam as unidades de trabalho que compõem cada milestone.

### Exemplo de rastreabilidade

```text
Milestone
    ↓
Issue
    ↓
Implementação
    ↓
Teste
    ↓
Validação
```

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
└── 09-delivery/
    └── delivery-checklist.md
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

# 🏗️ Estrutura do Repositório

```text
Distributed-Stock-Exchange-Simulator/
│
├── backend/
│
├── frontend/
│
├── tests/
│
├── docs/
│
├── README.md
│
└── .github/
```

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
