# Technology Decisions

## Distributed Stock Exchange Simulator

**Versão:** 1.0  
**Data:** 16/09/2026  
**Responsável:** Natália Silva Santos

---

# 1. Objetivo

Este documento registra as decisões tecnológicas adotadas para o desenvolvimento do projeto **Distributed Stock Exchange Simulator**, incluindo backend, frontend, persistência, comunicação, infraestrutura e documentação.

O objetivo é garantir consistência arquitetural durante todo o ciclo de desenvolvimento, reduzir riscos associados a mudanças tecnológicas futuras e fornecer uma referência única para as decisões arquiteturais do projeto.

---

# 2. Critérios Utilizados na Escolha

As tecnologias foram selecionadas considerando:

- Compatibilidade com os requisitos da disciplina de Sistemas Distribuídos;
- Facilidade de desenvolvimento individual;
- Conhecimento prévio da desenvolvedora;
- Disponibilidade de documentação e comunidade;
- Facilidade de demonstração para avaliação acadêmica;
- Possibilidade de evolução futura da arquitetura;
- Facilidade de implantação e manutenção.

---

# 3. Resumo das Decisões

| Categoria | Tecnologia |
|------------|------------|
| Linguagem | Java 21 (LTS) |
| Backend | Spring Boot |
| Build Tool | Maven |
| Banco de Dados | PostgreSQL |
| ORM | Spring Data JPA + Hibernate |
| Migrações | Flyway |
| Frontend | Vue |
| Comunicação REST | Spring Web (REST API) |
| Comunicação Tempo Real | WebSocket |
| Containerização | Docker |
| Orquestração Local | Docker Compose |
| Documentação API | OpenAPI 3 + Swagger UI |

---



# 4. Arquitetura Tecnológica Consolidada

```text
┌──────────────────────────┐
│          Vue UI          │
│       Home Broker        │
└────────────┬─────────────┘
             │
      REST + WebSocket
             │
┌────────────▼─────────────┐
│      Spring Boot API     │
│                          │
│ - Controllers REST       │
│ - Serviços de Negócio    │
│ - Gateway WebSocket      │
└────────────┬─────────────┘
             │
             ▼
┌──────────────────────────┐
│      Matching Engine     │
└────────────┬─────────────┘
             │
             ▼
┌──────────────────────────┐
│ PostgreSQL + Flyway      │
│ JPA / Hibernate          │
└──────────────────────────┘
```


# 5. Conclusão

As tecnologias selecionadas atendem aos requisitos técnicos e acadêmicos do projeto, oferecendo equilíbrio entre produtividade, manutenibilidade, escalabilidade e facilidade de demonstração.

As decisões registradas neste documento deverão servir como referência para todas as próximas etapas do desenvolvimento do **Distributed Stock Exchange Simulator** e somente poderão ser alteradas mediante nova decisão arquitetural formalmente documentada.