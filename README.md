# Service Order Manager

[![CI](https://github.com/ViiniDev/service-order-manager/actions/workflows/ci.yml/badge.svg)](https://github.com/ViiniDev/service-order-manager/actions/workflows/ci.yml)

Sistema fullstack para gestão de ordens de serviço, criado para demonstrar uma aplicação Java backend com frontend consumindo API real.

## Visão Geral

O projeto simula um fluxo de suporte técnico:

- cliente abre uma ordem de serviço;
- administrador acompanha indicadores e atribui um técnico;
- técnico atualiza status e registra comentários;
- todos os perfis visualizam apenas os dados permitidos para sua função.

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
- Cadastro de usuário pela API
- Criação de ordens de serviço
- Listagem por permissão de usuário
- Atribuição de técnico por administrador
- Atualização de status por administrador ou técnico
- Comentários e histórico da ordem
- Dashboard com contadores por status
- Dados iniciais para demonstração

## Diferenciais Técnicos

- Autenticação stateless com JWT.
- Autorização por perfil de usuário.
- Testes de integração com MockMvc cobrindo login, proteção de endpoints e ciclo de vida da ordem.
- Pipeline de CI com testes do backend, build do frontend e build dos containers.
- Documentação interativa da API com Swagger/OpenAPI.

## Usuários de Demonstração

Todos usam a senha:

```text
123456
```

| Perfil | Email |
| --- | --- |
| Admin | `admin@demo.com` |
| Técnico | `tecnico@demo.com` |
| Cliente | `cliente@demo.com` |

## Como Rodar com Docker

Na raiz do projeto:

```bash
docker-compose up --build
```

Aplicação web:

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

Para remover também os dados do banco:

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

### Atribuir Técnico

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

### Adicionar Comentário

```http
POST /api/orders/1/comments
Authorization: Bearer TOKEN
```

```json
{
  "message": "Diagnostico iniciado pelo tecnico."
}
```

## Testes e Builds

Backend:

```bash
cd backend
mvn test
```

Os testes atuais validam:

- login com usuário inicial;
- bloqueio de endpoint protegido sem token;
- criação de ordem por cliente;
- atribuição de técnico por administrador;
- alteração de status por técnico;
- comentário em ordem de serviço;
- bloqueio de alteração de status por cliente;
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

## Objetivo do Projeto

Este projeto foi criado para portifolio backend Java, demonstrando autenticação, autorização por perfil, regras de negócio, persistência, frontend integrado, Docker e documentação de execução.
