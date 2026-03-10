# 🏋️‍♂️ Personal Trainer System - Plataforma Fullstack de Gestão

Projeto desenvolvido para a disciplina de **Programação Web** Universidade Federal do Agreste de Pernambuco (UFAPE)

#### Equipe de Desenvolvimento:
- Brendo Brito
- Felipe Souza
- Thiago Mauricio

---

## 1. Visão Geral do Produto

O **Personal Trainer System** é uma plataforma Fullstack completa (API RESTful + Interface Web) desenvolvida para facilitar a gestão de personal trainers e o acompanhamento de seus alunos. O sistema permite o gerenciamento de perfis, planos de treino, avaliações físicas, faturas financeiras, catálogo de exercícios e comunicação direta via chat.

A arquitetura garante que o Administrador tenha controle global do sistema, enquanto os Personal Trainers têm um painel exclusivo para gerenciar apenas os alunos vinculados a eles.

---

## 2. Tecnologias Utilizadas 🛠️

O projeto foi construído separando as responsabilidades entre Backend e Frontend, utilizando tecnologias modernas do mercado.

### 2.1 Backend (API)
- **Linguagem:** Java 21
- **Framework:** Spring Boot 3.2.5
- **Segurança:** Spring Security + JWT (JSON Web Tokens)
- **Banco de Dados:** PostgreSQL (Produção) / H2 (Testes)
- **Documentação da API:** SpringDoc OpenAPI (Swagger UI)

### 2.2 Frontend (Interface Web)
- **Framework:** Next.js 16 (App Router)
- **Linguagem:** TypeScript
- **Estilização:** Tailwind CSS + Componentes Customizados
- **Comunicação:** Fetch API com interceptação de tokens JWT

---

## 3. Atores do Sistema 🔐

| Ator                | Nível de Acesso e Descrição                                                                 |
|---------------------|---------------------------------------------------------------------------------------------|
| **Administrador** | Acesso total. Pode visualizar e gerir todos os alunos do sistema e cadastrar categorias base (como Grupos Musculares). |
| **Personal Trainer**| Acesso restrito. Visualiza e gerencia **apenas** os alunos vinculados a ele. Pode montar treinos, acompanhar faturas e criar exercícios para sua biblioteca. |
| **Aluno** | Cliente final. Acessa seus planos de treino, avaliações físicas, histórico de faturas e chat com seu personal. |

---

## 4. Funcionalidades por Módulo ⚙️

### 4.1 Módulo de Usuários e Vínculos
- Autenticação e Autorização baseada em cargos (Roles) via JWT.
- Vinculação de aluno a um personal (ativa funcionalidades do aluno).
- Desvinculação de aluno (desativa funcionalidades).

### 4.2 Módulo de Exercícios e Grupos Musculares
- Grupos musculares pré-cadastrados no sistema.
- Catálogo de exercícios onde o Personal pode adicionar suas próprias variações e associá-las a um grupo muscular.

### 4.3 Módulo de Planos de Treino
- Criação de planos de treino com nome e validade (datas de início/fim).
- Limite de apenas um plano ativo por aluno.
- Adição detalhada de itens de treino (exercício, séries, repetições, carga, descanso).

### 4.4 Módulo de Faturas
- Controle financeiro automatizado com status: `PENDENTE`, `PAGA`, `CANCELADA`, `VENCIDA`.
- Limite de apenas uma fatura pendente por aluno.
- Bloqueio de exclusão: Um aluno não pode ser deletado do sistema se possuir faturas pendentes ou vencidas.

### 4.5 Módulo de Comunicação e Avaliação
- **Chat:** Comunicação isolada e restrita apenas entre o aluno e o seu personal vinculado.
- **Avaliações Físicas:** Registro de evolução contendo métricas como peso, altura, e percentual de gordura.

---

## 5. Casos de Teste e Qualidade 🧪

Os principais cenários foram validados rigorosamente e o backend possui testes unitários e de integração.

**Validações implementadas:**
- Prevenção de duplicidade (Email e CREF únicos).
- Vínculo exclusivo (Um aluno só pode ter um personal).
- Rotas protegidas (Personal não pode acessar rotas ou alunos de outros personais).

Para executar a suíte de testes do Java:
```bash
mvn test
```

---

## 6. Guia de Execução (Passo a Passo) 🚀

Para rodar o projeto localmente, você precisará subir o Banco de Dados, o Backend e o Frontend.

### Pré-requisitos
- **Java 21** e **Maven**
- **Node.js** (v18+) e **NPM**
- **PostgreSQL** rodando na porta padrão (5432)

### Passo 1: Configurar o Banco de Dados
Crie um banco de dados no PostgreSQL com as seguintes credenciais (configuradas no `application.properties`):
- **Nome do Banco:** `personal_trainer_db`
- **Usuário:** `postgres`
- **Senha:** `personaltrainer`

### Passo 2: Rodar o Backend (API Spring Boot)
1. Abra o terminal e navegue até a pasta raiz do backend (onde está o arquivo `pom.xml`).
2. Execute o comando do Maven para baixar as dependências e iniciar o servidor:
   ```bash
   mvn spring-boot:run
   ```
3. A API estará rodando em: `http://localhost:8080`
4. Acesse a **Documentação Swagger** em: `http://localhost:8080/swagger-ui.html`

### Passo 3: Rodar o Frontend (Next.js)
1. Abra um **novo terminal** e navegue até a pasta do frontend.
2. Instale todas as dependências do Node:
   ```bash
   npm install
   ```
3. Inicie o servidor de desenvolvimento web:
   ```bash
   npm run dev
   ```
4. Acesse o sistema pelo navegador em: `http://localhost:3000`

### 🔑 Credenciais Padrão de Teste
Para testar o sistema, você pode usar a conta de Administrador gerada automaticamente ao rodar o backend pela primeira vez:
- **E-mail:** `admin@gmail.com`
- **Senha:** `123456`