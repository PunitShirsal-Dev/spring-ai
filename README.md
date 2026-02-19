# Spring AI Project

Spring Boot 4.0.2 · Spring AI 2.0.0-M2 · Spring Security JWT

---

## Prerequisites

- Java 25
- Maven 3.9+
- OpenAI API Key

---

## Quick Start

**1. Clone and set your API key**

```bash
export OPENAI_API_KEY=sk-your-key-here
```

**2. Run**

```bash
./mvnw spring-boot:run
```

**3. Open Swagger UI**

```
http://localhost:8080/swagger-ui.html
```

---

## API Reference

### Auth — no token required

| Method | Endpoint           | Description          |
|--------|--------------------|----------------------|
| POST   | /api/auth/register | Register new user    |
| POST   | /api/auth/login    | Login, get tokens    |
| POST   | /api/auth/refresh  | Refresh access token |
| GET    | /api/auth/info     | Current user info    |

### Chat — Bearer token required

| Method | Endpoint                         | Description         |
|--------|----------------------------------|---------------------|
| POST   | /api/ai/chat                     | Blocking chat       |
| POST   | /api/ai/chat/stream              | Streaming SSE chat  |
| GET    | /api/ai/chat/history/{sessionId} | Get session history |
| DELETE | /api/ai/chat/history/{sessionId} | Clear session       |

### RAG — Bearer token required

| Method | Endpoint                | Description                  |
|--------|-------------------------|------------------------------|
| POST   | /api/ai/rag/ingest/pdf  | Upload PDF (ADMIN only)      |
| POST   | /api/ai/rag/ingest/text | Ingest raw text (ADMIN only) |
| POST   | /api/ai/rag/query       | Ask knowledge base           |

### Embedding — Bearer token required

| Method | Endpoint      | Description               |
|--------|---------------|---------------------------|
| POST   | /api/ai/embed | Generate embedding vector |

---

## Environment Variables

| Variable       | Required | Default          | Description     |
|----------------|----------|------------------|-----------------|
| OPENAI_API_KEY | Yes      | —                | Your OpenAI key |
| JWT_SECRET     | No       | Built-in default | Min 32 chars    |

---

## Run Tests

```bash
./mvnw test
```

---
