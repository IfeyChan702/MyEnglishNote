# Story Generation API Documentation

This document describes the new story generation API endpoints added to MyEnglishNote.

## Overview

The story generation feature allows users to:
1. Upload images and identify objects using Deepseek Vision API
2. Generate English fairy tales based on identified objects
3. Manage characters (protagonists) for stories
4. Search and manage generated stories
5. Share stories via unique tokens
6. Favorite stories

## Authentication

All endpoints require JWT authentication via the `Authorization` header:
```
Authorization: Bearer <jwt_token>
```

## API Endpoints

### Character Management

#### 1. Add Character
```http
POST /api/character/add
Content-Type: application/json

{
  "name": "Tom",
  "description": "A curious little boy",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

**Response:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "userId": 1,
    "name": "Tom",
    "description": "A curious little boy",
    "avatarUrl": "https://example.com/avatar.jpg",
    "storyCount": 0,
    "createdAt": "2025-02-11 10:00:00"
  }
}
```

#### 2. Get Character List
```http
GET /api/character/list?pageNum=1&pageSize=10
```

**Response:**
```json
{
  "total": 5,
  "rows": [
    {
      "id": 1,
      "name": "Tom",
      "description": "A curious little boy",
      "storyCount": 3
    }
  ],
  "code": 200,
  "msg": "查询成功"
}
```

#### 3. Get Character Details
```http
GET /api/character/{id}
```

#### 4. Update Character
```http
PUT /api/character/{id}
Content-Type: application/json

{
  "name": "Tom Jr.",
  "description": "Updated description"
}
```

#### 5. Delete Character
```http
DELETE /api/character/{id}
```

### Story Management

#### 1. Generate Story (Core Feature)
```http
POST /api/story/generate
Content-Type: application/json

{
  "image": "base64_encoded_image_data_or_url",
  "imageType": "base64",  // or "url"
  "characterId": 1,
  "title": "Tom's Adventure"  // optional
}
```

**Response:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "success": true,
    "storyId": 1,
    "title": "Tom's Adventure",
    "content": "Once upon a time, Tom found a magical book...",
    "objects": ["book", "lamp", "chair", "desk"],
    "characterName": "Tom",
    "imageUrl": null,
    "processingTime": 5234
  }
}
```

**Error Response:**
```json
{
  "code": 500,
  "msg": "生成故事失败: No objects identified in the image",
  "data": {
    "success": false,
    "errorMessage": "No objects identified in the image",
    "processingTime": 1234
  }
}
```

#### 2. Get Story List
```http
GET /api/story/list?pageNum=1&pageSize=10&characterId=1&isFavorite=1
```

**Query Parameters:**
- `characterId` (optional): Filter by character
- `isFavorite` (optional): Filter favorites (0 or 1)

**Response:**
```json
{
  "total": 10,
  "rows": [
    {
      "id": 1,
      "title": "Tom's Adventure",
      "content": "Once upon a time...",
      "objects": "[\"book\",\"lamp\"]",
      "characterName": "Tom",
      "isFavorite": 1,
      "viewCount": 15,
      "shareCount": 3,
      "createdAt": "2025-02-11 10:00:00"
    }
  ],
  "code": 200,
  "msg": "查询成功"
}
```

#### 3. Get Story Details
```http
GET /api/story/{id}
```

#### 4. Update Story
```http
PUT /api/story/{id}
Content-Type: application/json

{
  "title": "Updated Title",
  "content": "Updated content..."
}
```

#### 5. Delete Story
```http
DELETE /api/story/{id}
```

#### 6. Favorite Story
```http
POST /api/story/{id}/favorite
```

**Response:**
```json
{
  "code": 200,
  "msg": "收藏成功"
}
```

#### 7. Unfavorite Story
```http
DELETE /api/story/{id}/favorite
```

#### 8. Generate Share Link
```http
POST /api/story/{id}/share
```

**Response:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "shareToken": "abc123def456",
    "shareUrl": "/api/story/shared/abc123def456"
  }
}
```

#### 9. View Shared Story (No Auth Required)
```http
GET /api/story/shared/{shareToken}
```

**Response:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "title": "Tom's Adventure",
    "content": "Once upon a time...",
    "characterName": "Tom",
    "objects": "[\"book\",\"lamp\"]",
    "viewCount": 16
  }
}
```

### Search

#### 1. Search Stories
```http
POST /api/search/stories?keyword=adventure&threshold=0.7&maxResults=10
```

**Query Parameters:**
- `keyword` (optional): Search keyword
- `threshold` (optional): Similarity threshold (0-1), default: 0.7
- `maxResults` (optional): Maximum results, default: 10

**Response:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "title": "Tom's Adventure",
      "content": "Once upon a time...",
      "characterName": "Tom"
    }
  ]
}
```

#### 2. Search Characters
```http
POST /api/search/characters?keyword=tom
```

**Response:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "Tom",
      "description": "A curious little boy",
      "storyCount": 3
    }
  ]
}
```

#### 3. Global Search
```http
POST /api/search/all?keyword=adventure&threshold=0.7&maxResults=10
```

**Response:**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "stories": [...],
    "characters": [...],
    "totalStories": 5,
    "totalCharacters": 2
  }
}
```

## Database Schema

### Character Table
```sql
CREATE TABLE `character` (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(500),
  avatar_url VARCHAR(500),
  story_count INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag CHAR(1) DEFAULT '0'
);
```

### Story Table
```sql
CREATE TABLE story (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  character_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  content LONGTEXT NOT NULL,
  objects JSON,
  image_url VARCHAR(500),
  embedding JSON,
  embedding_model VARCHAR(100),
  is_favorite TINYINT DEFAULT 0,
  view_count INT DEFAULT 0,
  share_count INT DEFAULT 0,
  share_token VARCHAR(100) UNIQUE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  del_flag CHAR(1) DEFAULT '0',
  FOREIGN KEY (character_id) REFERENCES `character`(id) ON DELETE CASCADE
);
```

### Story Favorite Table
```sql
CREATE TABLE story_favorite (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  story_id BIGINT NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_story (user_id, story_id),
  FOREIGN KEY (story_id) REFERENCES story(id) ON DELETE CASCADE
);
```

## Error Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 400 | Bad Request (validation error) |
| 401 | Unauthorized (invalid token) |
| 403 | Forbidden (no permission) |
| 404 | Not Found |
| 500 | Internal Server Error |

## Usage Example

### Complete Flow: Generate a Story

1. **Create a character:**
```bash
curl -X POST http://localhost:9501/api/character/add \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice",
    "description": "A brave girl"
  }'
```

2. **Generate a story from an image:**
```bash
curl -X POST http://localhost:9501/api/story/generate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "image": "data:image/jpeg;base64,/9j/4AAQSkZJRg...",
    "imageType": "base64",
    "characterId": 1
  }'
```

3. **Get generated stories:**
```bash
curl -X GET "http://localhost:9501/api/story/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

4. **Share a story:**
```bash
curl -X POST http://localhost:9501/api/story/1/share \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Technical Details

### Image Processing
- Supports Base64 encoded images (JPEG, PNG, GIF)
- Supports direct image URLs
- Automatic image format detection
- Uses Deepseek Vision API for object recognition

### Story Generation
- Uses Deepseek Chat API
- Generates 200-300 word stories
- Includes all identified objects
- Educational and child-friendly content
- Simple English vocabulary for learners

### Vector Embeddings
- Automatic embedding generation for stories
- Uses Deepseek Embedding API
- Supports RAG-based semantic search
- Stored as JSON in database

### Performance
- Average story generation time: 3-8 seconds
- Includes retry mechanism with exponential backoff
- Configurable timeout (default: 30 seconds)
- Caching of embeddings

## Configuration

Add to `application-rag.yml`:
```yaml
rag:
  deepseek:
    api-key: ${DEEPSEEK_API_KEY}
    api-endpoint: https://api.deepseek.com
    embedding-path: /v1/embeddings
    chat-path: /v1/chat/completions
    embedding-model: deepseek-embedding
    chat-model: deepseek-chat
    multimodal-model: deepseek-chat
    timeout: 30
    max-retries: 3
```

## Security Considerations

1. **Authentication**: All endpoints require valid JWT token
2. **Authorization**: Users can only access their own stories and characters
3. **Input Validation**: All inputs are validated
4. **SQL Injection**: Protected by MyBatis parameter binding
5. **XSS**: Content is properly escaped in responses
6. **Rate Limiting**: Consider implementing rate limiting for story generation

## Future Enhancements

1. Support for multiple images per story
2. Story templates and themes
3. Audio narration of stories
4. Collaborative story writing
5. Story collections and albums
6. Export stories as PDF or ebook
7. Multi-language support
