# 🏋️‍♂️ Personal Trainer API - Sistema de Gestão para Personal Trainers

Projeto desenvolvido para a disciplina de **Programação Web**  
Universidade Federal do Agreste de Pernambuco (UFAPE)

---
## 1. Visão Geral do Produto

API RESTful desenvolvida como backend para um sistema de gestão voltado a personal trainers e seus alunos. O sistema permite o gerenciamento de alunos, planos de treino, itens de treino, avaliações físicas, faturas, exercícios, grupos musculares e comunicação via chat.

---

## 2. Tecnologias Utilizadas

### 2.1 Stack Tecnológica
- **Linguagem:** Java 21
- **Framework:** Spring Boot 3.2.5
- **Banco de Dados:** PostgreSQL (principal) / H2 (testes)
- **Documentação API:** SpringDoc OpenAPI (Swagger UI)

---

## 3. Atores do Sistema

| Ator                | Descrição                                                                 |
|---------------------|---------------------------------------------------------------------------|
| **Personal Trainer**| Profissional responsável pelo acompanhamento dos alunos                   |
| **Aluno**           | Cliente que contrata o personal trainer                                   |

---

## 4. Funcionalidades por Módulo

### 4.1 Módulo de Usuários
- Cadastro aberto de alunos e personais
- Vinculação de aluno a um personal (ativa funcionalidades do aluno)
- Desvinculação de aluno (desativa funcionalidades)

### 4.2 Módulo de Exercícios e Grupos Musculares
- Cadastro de grupos musculares
- Catálogo de exercícios com descrição e associação a grupo muscular
- Busca por nome ou grupo muscular

### 4.3 Módulo de Planos de Treino
- Criação de planos com nome, datas de início/fim
- Limite de apenas um plano ativo por aluno
- Adição livre de itens de treino (exercício, séries, repetições, carga, descanso)
- Permite repetição do mesmo exercício (útil para diferentes dias da semana)

### 4.4 Módulo de Avaliações Físicas
- Registro de métricas (peso, altura, percentual de gordura, observações, foto)
- Indicação se foi realizada pelo personal ou pelo aluno
- Histórico por aluno

### 4.5 Módulo de Faturas
- Controle de cobranças com valor, data de vencimento e status
- Status possíveis: PENDENTE, PAGA, CANCELADA, VENCIDA
- Limite de apenas uma fatura pendente por aluno
- Pagamento e cancelamento manual
- Vencimento automático (status muda para VENCIDA após a data)

### 4.6 Módulo de Chat
- Chat individual entre personal e aluno
- Criação automática e restrita: apenas entre aluno e seu personal vinculado
- Envio de mensagens de texto com timestamp
- Identificação do remetente (aluno ou personal)

---

## 5. Documentação da API

A API está totalmente documentada via **Swagger UI**.

Acesse após iniciar a aplicação:  
**http://localhost:8080/swagger-ui.html**

Permite testar todos os endpoints interativamente.

---
## 6. Casos de Teste (Regras de Negócio Testadas Manualmente)

Os principais cenários foram validados via Swagger/Postman:

- Cadastro de aluno e personal com validação de duplicidade (email/CREF)
- Vinculação/desvinculação aluno-personal
- Criação de plano com limite de 1 ativo por aluno
- Criação de fatura com limite de 1 pendente e vencimento automático
- Criação de chat apenas entre aluno e seu personal
- Envio de mensagens com identificação do remetente
- Restrições: aluno sem personal não pode ter plano, avaliação ou fatura

---
## 7. Testes

O projeto possui testes unitários e testes de integração.

Para executar os testes:

```bash
mvn test
```

## 8. Guia de Execução

### 8.1 Pré-requisitos
- Java 21
- Maven
- PostgreSQL

### 8.2 Configuração do Banco de Dados
- Banco: `personal_trainer_db`
- Usuário: `postgres`
- Senha: `personaltrainer`

### 8.3 Como Executar
```bash
mvn spring-boot:run