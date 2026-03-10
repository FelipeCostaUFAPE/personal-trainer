# 🏋️ Personal Trainer System  

Plataforma Fullstack de Gestão para Personal Trainers  
Projeto de Programação Web – UFAPE

#### Equipe de Desenvolvimento:
- Brendo Brito
- Felipe Souza
- Thiago Mauricio

---

## 1. Visão Geral do Produto

O Personal Trainer System é uma plataforma Fullstack completa (API RESTful + Interface Web) desenvolvida para facilitar a gestão diária de personal trainers e o acompanhamento personalizado de seus alunos. Com foco exclusivo na visão do personal trainer, a aplicação permite que o profissional gerencie de forma exclusiva os alunos vinculados a ele, crie e edite planos de treino com datas de validade, cadastre e associe exercícios a grupos musculares, acompanhe o status das faturas financeiras e realize todo o controle necessário para oferecer um atendimento eficiente e organizado.

---

## 2. Tecnologias Utilizadas 🛠️

### 2.1 Backend (API)
- **Linguagem:** Java 21
- **Framework:** Spring Boot 3.2.5
- **Segurança:** Spring Security + JWT (JSON Web Tokens)
- **Banco de Dados:** PostgreSQL (Produção) / H2 (Testes)

### 2.2 Frontend (Interface Web)
- **Framework:** Next.js 16 (App Router)
- **Linguagem:** TypeScript
- **Estilização:** Tailwind CSS + Componentes Customizados
- **Comunicação:** Fetch API com autenticação via JWT

---

## 3. Atores do Sistema 🔐

| Ator                | Nível de Acesso e Descrição                                                                 |
|---------------------|---------------------------------------------------------------------------------------------|
| **Administrador**   | Acesso total. Pode visualizar e gerenciar todos os alunos, personais e dados do sistema.   |
| **Personal Trainer**| Acesso restrito. Visualiza e gerencia os alunos vinculados a ele. Pode criar planos de treino, cadastrar exercícios e controlar faturas dos seus alunos. |

---

## 4. Funcionalidades por Módulo ⚙️

### 4.1 Módulo de Usuários e Vínculos
- Autenticação e autorização baseada em cargos (Roles) via JWT.
- Cadastro de personais e alunos.
- Vinculação de aluno a um personal (ativa funcionalidades do aluno).
- Desvinculação de aluno.
- Bloqueio de exclusão de aluno com faturas pendentes/vencidas ou planos ativos.

### 4.2 Módulo de Exercícios e Grupos Musculares
- Cadastro de grupos musculares (apenas admin).
- Catálogo de exercícios (personal pode adicionar e associar a grupos musculares).

### 4.3 Módulo de Planos de Treino
- Criação de planos de treino com nome, datas de início/fim e vinculação a aluno.
- Adição de itens de treino (exercício, séries, repetições, carga, descanso).
- Visualização de planos por personal (apenas dos seus alunos).

### 4.4 Módulo de Faturas
- Controle financeiro com status: `PENDENTE`, `PAGA`, `CANCELADA`, `VENCIDA`.
- Limite de uma fatura pendente por aluno.
- Pagamento e cancelamento de faturas.
- Visualização de faturas por personal (apenas dos seus alunos).

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

### Passo 3: Rodar o Frontend (Next.js)
1. Abra um novo terminal e navegue até a pasta do frontend.
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