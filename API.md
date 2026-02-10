# MyEnglishNote - API Documentation

Complete REST API reference for the MyEnglishNote backend.

## Table of Contents

- [Overview](#overview)
- [Authentication](#authentication)
- [Common Response Format](#common-response-format)
- [Error Codes](#error-codes)
- [Note APIs](#note-apis)
- [RAG APIs](#rag-apis)
- [Review APIs](#review-apis)
- [Examples](#examples)

## Overview

**Base URL**: `http://localhost:9501`

**API Prefix**: `/api`

**Content-Type**: `application/json`

**Authentication**: JWT Bearer Token (except login/register)

## Authentication

### Login

**Endpoint**: `POST /login`

**Request Body**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response** (Success - 200):
```json
{
  "code": 200,
  "msg": "操作成功",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": 1
}
```

**Example**:
```bash
curl -X POST http://localhost:9501/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Register

**Endpoint**: `POST /register`

**Request Body**:
```json
{
  "username": "newuser",
  "password": "password123",
  "email": "user@example.com"
}
```

**Response** (Success - 200):
```json
{
  "code": 200,
  "msg": "注册成功"
}
```

### Using Authentication

Include the token in all subsequent requests:

```bash
curl -X GET http://localhost:9501/api/note/list \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Common Response Format

### Success Response

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    // Response data
  }
}
```

### Error Response

```json
{
  "code": 500,
  "msg": "错误信息"
}
```

### Paginated Response

```json
{
  "code": 200,
  "msg": "查询成功",
  "rows": [
    // Array of items
  ],
  "total": 100
}
```

## Error Codes

| Code | Description |
|------|-------------|
| 200  | Success |
| 401  | Unauthorized (invalid/expired token) |
| 403  | Forbidden (no permission) |
| 404  | Not Found |
| 500  | Internal Server Error |

## Note APIs

### Create Note

**Endpoint**: `POST /api/note/add`

**Headers**:
- `Authorization: Bearer {token}`
- `Content-Type: application/json`

**Request Body**:
```json
{
  "content": "apple - 苹果, a red or green fruit",
  "tags": "vocabulary,fruit,food"
}
```

**Response** (200):
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "userId": 1,
    "content": "apple - 苹果, a red or green fruit",
    "embedding": [0.123, 0.456, ...],  // 1024-dimensional vector
    "embeddingModel": "deepseek-embedding",
    "tags": "vocabulary,fruit,food",
    "createdAt": "2025-02-10 10:30:00",
    "updatedAt": "2025-02-10 10:30:00",
    "delFlag": "0"
  }
}
```

**Example**:
```bash
curl -X POST http://localhost:9501/api/note/add \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "apple - 苹果, a red fruit",
    "tags": "vocabulary,fruit"
  }'
```

### List Notes

**Endpoint**: `GET /api/note/list`

**Headers**:
- `Authorization: Bearer {token}`

**Query Parameters**:
- `pageNum` (optional, default: 1): Page number
- `pageSize` (optional, default: 10): Items per page

**Response** (200):
```json
{
  "code": 200,
  "msg": "查询成功",
  "rows": [
    {
      "id": 1,
      "userId": 1,
      "content": "apple - 苹果",
      "embedding": [...],
      "embeddingModel": "deepseek-embedding",
      "tags": "vocabulary,fruit",
      "createdAt": "2025-02-10 10:30:00",
      "updatedAt": "2025-02-10 10:30:00",
      "delFlag": "0"
    }
  ],
  "total": 50
}
```

**Example**:
```bash
curl -X GET "http://localhost:9501/api/note/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Get Note by ID

**Endpoint**: `GET /api/note/{id}`

**Headers**:
- `Authorization: Bearer {token}`

**Path Parameters**:
- `id`: Note ID

**Response** (200):
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "userId": 1,
    "content": "apple - 苹果",
    "embedding": [...],
    "tags": "vocabulary",
    "createdAt": "2025-02-10 10:30:00",
    "updatedAt": "2025-02-10 10:30:00"
  }
}
```

**Example**:
```bash
curl -X GET http://localhost:9501/api/note/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Update Note

**Endpoint**: `PUT /api/note/{id}`

**Headers**:
- `Authorization: Bearer {token}`
- `Content-Type: application/json`

**Path Parameters**:
- `id`: Note ID

**Request Body**:
```json
{
  "content": "apple - 苹果 (updated)",
  "tags": "vocabulary,fruit,updated"
}
```

**Response** (200):
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "userId": 1,
    "content": "apple - 苹果 (updated)",
    "embedding": [...],  // Re-generated
    "tags": "vocabulary,fruit,updated",
    "updatedAt": "2025-02-10 11:00:00"
  }
}
```

**Example**:
```bash
curl -X PUT http://localhost:9501/api/note/1 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "apple - 苹果 (updated)",
    "tags": "vocabulary"
  }'
```

### Delete Note

**Endpoint**: `DELETE /api/note/{id}`

**Headers**:
- `Authorization: Bearer {token}`

**Path Parameters**:
- `id`: Note ID

**Response** (200):
```json
{
  "code": 200,
  "msg": "操作成功"
}
```

**Example**:
```bash
curl -X DELETE http://localhost:9501/api/note/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Batch Delete Notes

**Endpoint**: `DELETE /api/note/batch`

**Headers**:
- `Authorization: Bearer {token}`
- `Content-Type: application/json`

**Request Body**:
```json
[1, 2, 3, 4, 5]
```

**Response** (200):
```json
{
  "code": 200,
  "msg": "操作成功"
}
```

**Example**:
```bash
curl -X DELETE http://localhost:9501/api/note/batch \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '[1, 2, 3]'
```

### Count Notes

**Endpoint**: `GET /api/note/count`

**Headers**:
- `Authorization: Bearer {token}`

**Response** (200):
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 42
}
```

**Example**:
```bash
curl -X GET http://localhost:9501/api/note/count \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## RAG APIs

### Vector Search

**Endpoint**: `POST /api/rag/search`

**Headers**:
- `Authorization: Bearer {token}`
- `Content-Type: application/json`

**Request Body**:
```json
{
  "question": "What is apple?",
  "similarityThreshold": 0.7,
  "maxResults": 5
}
```

**Parameters**:
- `question` (required): Search query
- `similarityThreshold` (optional, default: 0.7): Minimum similarity score (0-1)
- `maxResults` (optional, default: 5): Maximum number of results

**Response** (200):
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "content": "apple - 苹果, a red fruit",
      "similarity": 0.95,
      "tags": "vocabulary,fruit"
    },
    {
      "id": 5,
      "content": "red apple - 红苹果",
      "similarity": 0.82,
      "tags": "vocabulary"
    }
  ]
}
```

**Example**:
```bash
curl -X POST http://localhost:9501/api/rag/search \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What is apple?",
    "similarityThreshold": 0.7,
    "maxResults": 5
  }'
```

### Get AI Answer

**Endpoint**: `POST /api/rag/answer`

**Headers**:
- `Authorization: Bearer {token}`
- `Content-Type: application/json`

**Request Body**:
```json
{
  "question": "What is the difference between apple and banana?",
  "similarityThreshold": 0.7,
  "maxResults": 5,
  "includeContext": true
}
```

**Parameters**:
- `question` (required): Your question
- `similarityThreshold` (optional): Similarity threshold
- `maxResults` (optional): Max related notes
- `includeContext` (optional, default: true): Include context in answer

**Response** (200):
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "success": true,
    "question": "What is the difference between apple and banana?",
    "answer": "Based on your notes:\n\nApple (苹果) is typically red or green and is often described as crisp. Banana (香蕉) is yellow and has a softer texture. Both are fruits, but they have different tastes and nutritional profiles.",
    "relatedNotes": [
      {
        "id": 1,
        "content": "apple - 苹果, a red or green fruit",
        "tags": "vocabulary,fruit"
      },
      {
        "id": 2,
        "content": "banana - 香蕉, a yellow fruit",
        "tags": "vocabulary,fruit"
      }
    ],
    "processingTime": 1234,
    "similarityThreshold": 0.7,
    "maxResults": 5
  }
}
```

**Error Response**:
```json
{
  "code": 500,
  "msg": "生成回答失败",
  "data": {
    "success": false,
    "question": "What is apple?",
    "errorMessage": "Deepseek API error: Rate limit exceeded",
    "processingTime": 0
  }
}
```

**Example**:
```bash
curl -X POST http://localhost:9501/api/rag/answer \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "Explain the word apple",
    "includeContext": true
  }'
```

### Chat (Multi-turn)

**Endpoint**: `POST /api/rag/chat`

**Headers**:
- `Authorization: Bearer {token}`
- `Content-Type: application/json`

**Request Body**:
```json
{
  "question": "Tell me more about fruits",
  "similarityThreshold": 0.7,
  "maxResults": 5
}
```

**Response**: Same format as `/api/rag/answer`

**Note**: Currently uses same implementation as `answer`. Future versions will support conversation history.

**Example**:
```bash
curl -X POST http://localhost:9501/api/rag/chat \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What fruits do I have notes about?"
  }'
```

## Review APIs

### Get Next Review Items

**Endpoint**: `GET /api/review/next`

**Headers**:
- `Authorization: Bearer {token}`

**Query Parameters**:
- `limit` (optional, default: 1): Number of items to return

**Response** (200 - Single Item):
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "noteId": 5,
    "userId": 1,
    "quality": 4,
    "easinessFactor": 2.5,
    "intervalDays": 1,
    "repetitions": 0,
    "nextReviewDate": "2025-02-10 10:00:00",
    "reviewedAt": "2025-02-09 10:00:00"
  }
}
```

**Response** (200 - Multiple Items):
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "noteId": 5,
      ...
    },
    {
      "id": 2,
      "noteId": 8,
      ...
    }
  ]
}
```

**Response** (200 - No Items):
```json
{
  "code": 200,
  "msg": "暂无待复习项目",
  "data": null
}
```

**Example**:
```bash
curl -X GET "http://localhost:9501/api/review/next?limit=1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Record Review Result

**Endpoint**: `POST /api/review/record`

**Headers**:
- `Authorization: Bearer {token}`

**Query Parameters**:
- `noteId` (required): Note ID
- `quality` (required): Review quality (0-5)

**Quality Scale**:
- 0: Complete blackout
- 1: Incorrect, but familiar
- 2: Incorrect, but easy to recall
- 3: Correct, but difficult
- 4: Correct, with hesitation
- 5: Perfect recall

**Response** (200):
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 2,
    "noteId": 5,
    "userId": 1,
    "quality": 4,
    "easinessFactor": 2.6,
    "intervalDays": 6,
    "repetitions": 1,
    "nextReviewDate": "2025-02-16 10:00:00",
    "reviewedAt": "2025-02-10 10:00:00"
  }
}
```

**Example**:
```bash
curl -X POST "http://localhost:9501/api/review/record?noteId=5&quality=4" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Get Review Statistics

**Endpoint**: `GET /api/review/stats`

**Headers**:
- `Authorization: Bearer {token}`

**Response** (200):
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "totalReviews": 150,
    "dueToday": 5,
    "dueThisWeek": 23,
    "dueThisMonth": 67,
    "averageQuality": 4.2,
    "streak": 7
  }
}
```

**Example**:
```bash
curl -X GET http://localhost:9501/api/review/stats \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Initialize Review

**Endpoint**: `POST /api/review/initialize`

**Headers**:
- `Authorization: Bearer {token}`

**Query Parameters**:
- `noteId` (required): Note ID

**Response** (200):
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "noteId": 5,
    "userId": 1,
    "quality": 0,
    "easinessFactor": 2.5,
    "intervalDays": 1,
    "repetitions": 0,
    "nextReviewDate": "2025-02-11 10:00:00",
    "reviewedAt": "2025-02-10 10:00:00"
  }
}
```

**Example**:
```bash
curl -X POST "http://localhost:9501/api/review/initialize?noteId=5" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Examples

### Complete Workflow Example

```bash
# 1. Login
TOKEN=$(curl -s -X POST http://localhost:9501/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.token')

# 2. Create a note
curl -X POST http://localhost:9501/api/note/add \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "computer - 计算机, an electronic device",
    "tags": "vocabulary,technology"
  }'

# 3. List notes
curl -X GET http://localhost:9501/api/note/list \
  -H "Authorization: Bearer $TOKEN"

# 4. Search with RAG
curl -X POST http://localhost:9501/api/rag/answer \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What is a computer?",
    "includeContext": true
  }'

# 5. Initialize review for note
curl -X POST "http://localhost:9501/api/review/initialize?noteId=1" \
  -H "Authorization: Bearer $TOKEN"

# 6. Get next review
curl -X GET http://localhost:9501/api/review/next \
  -H "Authorization: Bearer $TOKEN"

# 7. Record review
curl -X POST "http://localhost:9501/api/review/record?noteId=1&quality=5" \
  -H "Authorization: Bearer $TOKEN"
```

### Python Example

```python
import requests

BASE_URL = "http://localhost:9501"

# Login
response = requests.post(f"{BASE_URL}/login", json={
    "username": "admin",
    "password": "admin123"
})
token = response.json()["token"]

# Create headers
headers = {
    "Authorization": f"Bearer {token}",
    "Content-Type": "application/json"
}

# Create note
response = requests.post(
    f"{BASE_URL}/api/note/add",
    headers=headers,
    json={
        "content": "apple - 苹果",
        "tags": "vocabulary"
    }
)
note = response.json()["data"]
print(f"Created note: {note['id']}")

# RAG search
response = requests.post(
    f"{BASE_URL}/api/rag/answer",
    headers=headers,
    json={
        "question": "What is apple?",
        "includeContext": True
    }
)
answer = response.json()["data"]
print(f"Answer: {answer['answer']}")
```

### JavaScript/TypeScript Example

```javascript
const BASE_URL = 'http://localhost:9501';

// Login
const loginResponse = await fetch(`${BASE_URL}/login`, {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'admin',
    password: 'admin123'
  })
});
const { token } = await loginResponse.json();

// Create note
const noteResponse = await fetch(`${BASE_URL}/api/note/add`, {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    content: 'apple - 苹果',
    tags: 'vocabulary'
  })
});
const note = await noteResponse.json();
console.log('Created note:', note.data);

// RAG search
const ragResponse = await fetch(`${BASE_URL}/api/rag/answer`, {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    question: 'What is apple?',
    includeContext: true
  })
});
const answer = await ragResponse.json();
console.log('Answer:', answer.data.answer);
```

## Rate Limiting

Currently no rate limiting is implemented. In production, consider:
- Request rate limits per user
- API key quotas
- Deepseek API rate limits

## Versioning

API version is not explicitly specified. All endpoints are in v1.
Future versions may include `/api/v2/...` prefix.

## Support

For API-related questions:
- Check Swagger UI: `http://localhost:9501/swagger-ui.html`
- Review backend logs
- Check GitHub issues
