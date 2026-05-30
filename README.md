# Service Order Manager

[![CI](https://github.com/ViiniDev/service-order-manager/actions/workflows/ci.yml/badge.svg)](https://github.com/ViiniDev/service-order-manager/actions/workflows/ci.yml)

Sistema fullstack para gestao de ordens de servico, criado para demonstrar uma aplicacao Java backend com frontend consumindo API real.

## Visao Geral

O projeto simula um fluxo de suporte tecnico:

- cliente abre uma ordem de servico;
- administrador acompanha indicadores e atribui um tecnico;
- tecnico atualiza status e registra comentarios;
- todos os perfis visualizam apenas os dados permitidos para sua funcao.

## Tecnologias

### Backend

- Java 17
- Spring Boot 3.3
- Spring Security
- JWT
- Spring Data JPA
- Bean Validation
- Swagger/OpenAPI
- H2 para desenvolvimento local
- PostgreSQL no Docker
- Maven

### Frontend

- React
- TypeScript
- Vite
- CSS responsivo

### Infra

- Docker
- Docker Compose
- Nginx para servir o frontend em container
- GitHub Actions para CI

## Funcionalidades

- Login com JWT
- Perfis `ADMIN`, `TECHNICIAN` e `CLIENT`
- Cadastro de usuario pela API
- Criacao de ordens de servico
- Listagem por permissao de usuario
- Atribuicao de tecnico por administrador
- Atualizacao de status por administrador ou tecnico
- Comentarios e historico da ordem
- Dashboard com contadores por status
- Dados iniciais para demonstracao

## Diferenciais Tecnicos

- Autenticacao stateless com JWT.
- Autorizacao por perfil de usuario.
- Testes de integracao com MockMvc cobrindo login, protecao de endpoints e ciclo de vida da ordem.
- Pipeline de CI com testes do backend, build do frontend e build dos containers.
- Documentacao interativa da API com Swagger/OpenAPI.

## Usuarios De Demonstracao

Todos usam a senha:

```text
123456
```

| Perfil | Email |
| --- | --- |
| Admin | `admin@demo.com` |
| Tecnico | `tecnico@demo.com` |
| Cliente | `cliente@demo.com` |

## Como Rodar Com Docker

Na raiz do projeto:

```bash
docker-compose up --build
```

Aplicacao web:

```text
http://localhost:3000
```

API:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/docs
```

Para parar:

```bash
docker-compose down
```

Para remover tambem os dados do banco:

```bash
docker-compose down -v
```

## Como Rodar Localmente

### Backend

```bash
cd backend
mvn clean package
java -jar target/service-order-manager-api-0.0.1-SNAPSHOT.jar
```

API local:

```text
http://localhost:8080
```

Swagger local:

```text
http://localhost:8080/docs
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend local:

```text
http://localhost:5173
```

## Endpoints Principais

### Login

```http
POST /api/auth/login
```

```json
{
  "email": "admin@demo.com",
  "password": "123456"
}
```

### Criar Ordem

```http
POST /api/orders
Authorization: Bearer TOKEN
```

```json
{
  "title": "Computador nao inicializa",
  "description": "Equipamento nao passa da tela inicial.",
  "priority": "HIGH"
}
```

### Atribuir Tecnico

```http
PATCH /api/orders/1/assign
Authorization: Bearer TOKEN
```

```json
{
  "technicianId": 2
}
```

### Atualizar Status

```http
PATCH /api/orders/1/status
Authorization: Bearer TOKEN
```

```json
{
  "status": "IN_PROGRESS"
}
```

### Adicionar Comentario

```http
POST /api/orders/1/comments
Authorization: Bearer TOKEN
```

```json
{
  "message": "Diagnostico iniciado pelo tecnico."
}
```

## Testes E Builds

Backend:

```bash
cd backend
mvn test
```

Os testes atuais validam:

- login com usuario inicial;
- bloqueio de endpoint protegido sem token;
- criacao de ordem por cliente;
- atribuicao de tecnico por administrador;
- alteracao de status por tecnico;
- comentario em ordem de servico;
- bloqueio de alteracao de status por cliente;
- dashboard com dados consolidados.

Frontend:

```bash
cd frontend
npm run build
```

## CI/CD

O projeto possui workflow no GitHub Actions em `.github/workflows/ci.yml`.

O pipeline executa:

- testes automatizados do backend com Maven;
- build do frontend com npm;
- build das imagens Docker via Docker Compose.

## Objetivo Do Projeto

Este projeto foi criado para portifolio backend Java, demonstrando autenticacao, autorizacao por perfil, regras de negocio, persistencia, frontend integrado, Docker e documentacao de execucao.
