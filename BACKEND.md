# MyEnglishNote - Backend Development Documentation

This document provides comprehensive information about the backend architecture, development practices, and implementation details.

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Core Components](#core-components)
- [Database Design](#database-design)
- [RAG Pipeline](#rag-pipeline)
- [SRS Algorithm](#srs-algorithm)
- [Development Guide](#development-guide)
- [Testing](#testing)
- [Best Practices](#best-practices)

## Architecture Overview

The backend follows a layered architecture pattern:

```
┌─────────────────────────────────────┐
│     Controller Layer (REST API)     │
├─────────────────────────────────────┤
│     Service Layer (Business Logic)  │
├─────────────────────────────────────┤
│     Mapper Layer (Data Access)      │
├─────────────────────────────────────┤
│     Database (MySQL)                │
└─────────────────────────────────────┘
```

### Key Features

- **RESTful API**: Clean REST API design with proper HTTP methods
- **JWT Authentication**: Secure token-based authentication
- **MyBatis**: Flexible SQL mapping framework
- **Spring Security**: Comprehensive security framework
- **RAG Integration**: Deepseek API for embeddings and AI responses
- **SRS System**: SuperMemo 2 algorithm for spaced repetition

## Technology Stack

### Core Framework
- **Spring Boot**: 2.5.15
- **Spring Security**: 5.7.14
- **Spring Framework**: 5.3.39

### Data Access
- **MyBatis**: Latest version
- **MySQL**: 8.0+
- **Druid**: Database connection pool
- **Redis**: Caching layer

### API & Documentation
- **Swagger**: 3.0.0 (API documentation)
- **JWT**: 0.9.1 (JSON Web Tokens)

### Build Tools
- **Maven**: 3.6+
- **Java**: 17

## Project Structure

```
ruoyi-admin/          # Main application module
├── src/main/
│   ├── java/com/ruoyi/
│   │   └── web/
│   │       └── controller/
│   │           ├── rag/                    # RAG controllers
│   │           │   ├── NoteController.java
│   │           │   ├── RAGController.java
│   │           │   └── ReviewController.java
│   │           ├── system/                 # System controllers
│   │           └── common/                 # Common controllers
│   └── resources/
│       ├── application.yml                 # Main config
│       ├── application-druid.yml          # Database config
│       ├── application-rag.yml            # RAG config
│       └── mapper/                        # MyBatis mappers
│
ruoyi-system/         # System module
├── src/main/
│   └── java/com/ruoyi/system/
│       ├── domain/                         # Domain models
│       │   ├── EnglishNote.java
│       │   ├── ReviewRecord.java
│       │   └── dto/
│       │       ├── RAGQueryRequest.java
│       │       └── RAGResponse.java
│       ├── service/                        # Service interfaces
│       │   ├── INoteService.java
│       │   ├── IRAGService.java
│       │   └── IReviewService.java
│       ├── service/impl/                   # Service implementations
│       │   ├── NoteServiceImpl.java
│       │   ├── RAGServiceImpl.java
│       │   └── ReviewServiceImpl.java
│       └── mapper/                         # Data mappers
│           ├── NoteMapper.java
│           ├── EmbeddingMapper.java
│           └── ReviewMapper.java
│
ruoyi-common/         # Common utilities
ruoyi-framework/      # Framework integration
ruoyi-generator/      # Code generator
ruoyi-quartz/         # Scheduled tasks
```

## Core Components

### 1. Note Controller

**Location**: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/rag/NoteController.java`

**Endpoints**:
- `POST /api/note/add` - Create note with auto-embedding generation
- `GET /api/note/list` - List user notes with pagination
- `GET /api/note/{id}` - Get note details
- `PUT /api/note/{id}` - Update note (regenerates embedding)
- `DELETE /api/note/{id}` - Delete note
- `DELETE /api/note/batch` - Batch delete notes
- `GET /api/note/count` - Count user notes

**Key Features**:
- Automatic vector embedding generation on create/update
- User ownership validation
- Pagination support
- Soft delete support

### 2. RAG Controller

**Location**: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/rag/RAGController.java`

**Endpoints**:
- `POST /api/rag/search` - Vector similarity search
- `POST /api/rag/answer` - Get AI-generated answer with context
- `POST /api/rag/chat` - Multi-turn conversation (future enhancement)

**Request Format**:
```json
{
  "question": "What is the meaning of apple?",
  "similarityThreshold": 0.7,
  "maxResults": 5,
  "includeContext": true
}
```

**Response Format**:
```json
{
  "success": true,
  "question": "What is the meaning of apple?",
  "answer": "AI-generated answer...",
  "relatedNotes": [...],
  "processingTime": 1234,
  "similarityThreshold": 0.7,
  "maxResults": 5
}
```

### 3. Review Controller

**Location**: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/rag/ReviewController.java`

**Endpoints**:
- `GET /api/review/next?limit=1` - Get next review items
- `POST /api/review/record?noteId=1&quality=4` - Record review result
- `GET /api/review/stats` - Get review statistics
- `POST /api/review/initialize?noteId=1` - Initialize review for note

**Quality Scale** (0-5):
- 0: Complete blackout
- 1: Incorrect, but familiar
- 2: Incorrect, but easy to recall
- 3: Correct, but difficult
- 4: Correct, with hesitation
- 5: Perfect recall

## Database Design

### English Note Table

```sql
CREATE TABLE english_note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    embedding JSON DEFAULT NULL,
    embedding_model VARCHAR(100) DEFAULT 'deepseek-embedding',
    tags VARCHAR(500) DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag CHAR(1) DEFAULT '0',
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_del_flag (del_flag)
);
```

### Review Record Table

```sql
CREATE TABLE review_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    quality INT NOT NULL,
    easiness_factor DECIMAL(5,2) DEFAULT 2.50,
    interval_days INT DEFAULT 1,
    repetitions INT DEFAULT 0,
    next_review_date DATETIME NOT NULL,
    reviewed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_note_id (note_id),
    INDEX idx_user_id (user_id),
    INDEX idx_next_review (next_review_date),
    FOREIGN KEY (note_id) REFERENCES english_note(id) ON DELETE CASCADE
);
```

### Cosine Similarity Function

```sql
CREATE FUNCTION cosine_similarity(vec1 JSON, vec2 JSON) 
RETURNS DECIMAL(10,6)
DETERMINISTIC
BEGIN
    -- Calculate cosine similarity between two vectors
    DECLARE dot_product DECIMAL(20,10) DEFAULT 0;
    DECLARE magnitude1 DECIMAL(20,10) DEFAULT 0;
    DECLARE magnitude2 DECIMAL(20,10) DEFAULT 0;
    -- Implementation details in sql/rag_init.sql
END;
```

## RAG Pipeline

### 1. Embedding Generation

When a note is created or updated:

```java
// 1. Generate embedding from Deepseek API
DeepseekEmbeddingRequest request = new DeepseekEmbeddingRequest();
request.setInput(note.getContent());
request.setModel("deepseek-embedding");

DeepseekEmbeddingResponse response = 
    deepseekApiService.generateEmbedding(request);

// 2. Store embedding as JSON
note.setEmbedding(response.getData().get(0).getEmbedding());
noteMapper.insert(note);
```

### 2. Vector Search

```java
// 1. Generate query embedding
List<Double> queryEmbedding = 
    deepseekApiService.generateEmbedding(question);

// 2. Search similar notes using cosine similarity
List<EnglishNote> similarNotes = noteMapper.findSimilarNotes(
    userId, 
    queryEmbedding, 
    similarityThreshold, 
    maxResults
);
```

### 3. Context-Aware Answer Generation

```java
// 1. Retrieve relevant notes
List<EnglishNote> context = ragService.searchNotes(userId, question);

// 2. Build prompt with context
String prompt = buildPromptWithContext(question, context);

// 3. Get AI answer from Deepseek
DeepseekChatRequest chatRequest = new DeepseekChatRequest();
chatRequest.setMessages([
    {role: "system", content: "You are a helpful English teacher..."},
    {role: "user", content: prompt}
]);

DeepseekChatResponse response = 
    deepseekApiService.chat(chatRequest);

return response.getChoices().get(0).getMessage().getContent();
```

## SRS Algorithm

### SuperMemo 2 Implementation

```java
public ReviewRecord calculateNextReview(int quality, ReviewRecord current) {
    // Quality: 0-5
    // EF: Easiness Factor (1.3-2.5)
    // I: Interval in days
    // R: Repetitions
    
    double EF = current.getEasinessFactor();
    int I = current.getIntervalDays();
    int R = current.getRepetitions();
    
    // Update EF
    EF = EF + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));
    if (EF < 1.3) EF = 1.3;
    
    // Update interval
    if (quality < 3) {
        // Failed: reset
        R = 0;
        I = 1;
    } else {
        R++;
        if (R == 1) {
            I = 1;
        } else if (R == 2) {
            I = 6;
        } else {
            I = (int) Math.round(I * EF);
        }
    }
    
    // Calculate next review date
    Date nextReview = DateUtils.addDays(new Date(), I);
    
    return new ReviewRecord(noteId, userId, quality, EF, I, R, nextReview);
}
```

## Development Guide

### Setup Development Environment

1. **Install Prerequisites**:
   ```bash
   # Java 17
   java -version
   
   # Maven
   mvn -version
   
   # MySQL
   mysql --version
   ```

2. **Clone Repository**:
   ```bash
   git clone https://github.com/IfeyChan702/MyEnglishNote.git
   cd MyEnglishNote
   ```

3. **Import Database**:
   ```bash
   mysql -u root -p < sql/ry_20250522.sql
   mysql -u root -p < sql/quartz.sql
   mysql -u root -p < sql/rag_init.sql
   ```

4. **Configure Application**:
   Edit `ruoyi-admin/src/main/resources/application-druid.yml`
   Edit `ruoyi-admin/src/main/resources/application-rag.yml`

5. **Build and Run**:
   ```bash
   mvn clean install
   cd ruoyi-admin
   mvn spring-boot:run
   ```

### Adding New Features

1. **Create Domain Model** (`ruoyi-system/domain/`)
2. **Create Mapper Interface** (`ruoyi-system/mapper/`)
3. **Create Mapper XML** (`ruoyi-system/resources/mapper/`)
4. **Create Service Interface** (`ruoyi-system/service/`)
5. **Implement Service** (`ruoyi-system/service/impl/`)
6. **Create Controller** (`ruoyi-admin/controller/`)
7. **Add Swagger Documentation**

### Code Style

- Use Spring Boot conventions
- Follow Java naming conventions
- Add JavaDoc for public methods
- Use Lombok to reduce boilerplate
- Implement proper exception handling

## Testing

### Unit Tests

```java
@SpringBootTest
public class NoteServiceTest {
    @Autowired
    private INoteService noteService;
    
    @Test
    public void testCreateNote() {
        EnglishNote note = new EnglishNote();
        note.setContent("Test content");
        note.setUserId(1L);
        
        EnglishNote created = noteService.createNote(note);
        assertNotNull(created.getId());
        assertNotNull(created.getEmbedding());
    }
}
```

### Integration Tests

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class NoteControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser
    public void testGetNotes() throws Exception {
        mockMvc.perform(get("/api/note/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
}
```

### API Testing

Use Swagger UI: `http://localhost:9501/swagger-ui.html`

Or use curl:

```bash
# Login
curl -X POST http://localhost:9501/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Create note
curl -X POST http://localhost:9501/api/note/add \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"apple - 苹果"}'
```

## Best Practices

1. **Security**
   - Always validate user ownership
   - Use parameterized queries
   - Sanitize user input
   - Implement rate limiting

2. **Performance**
   - Use pagination for large datasets
   - Cache frequently accessed data
   - Optimize database queries
   - Use async processing for heavy operations

3. **Error Handling**
   - Use proper HTTP status codes
   - Return meaningful error messages
   - Log errors with context
   - Implement global exception handler

4. **API Design**
   - Follow REST conventions
   - Version your APIs
   - Document all endpoints
   - Use consistent response format

5. **Code Quality**
   - Write unit tests
   - Use code reviews
   - Follow SOLID principles
   - Keep methods small and focused

## Deepseek API Integration

### Configuration

```yaml
deepseek:
  api:
    key: ${DEEPSEEK_API_KEY}
    base-url: https://api.deepseek.com
    timeout:
      connect: 30000
      read: 60000
    model:
      embedding: deepseek-embedding
      chat: deepseek-chat
```

### Usage Example

```java
@Service
public class DeepseekApiService {
    
    public List<Double> generateEmbedding(String text) {
        // Call Deepseek API to generate embedding
        // Returns a 1024-dimensional vector
    }
    
    public String chat(String prompt, List<Message> history) {
        // Call Deepseek API for chat completion
        // Supports multi-turn conversation
    }
}
```

## Common Issues and Solutions

### Issue: Embedding Generation Fails

**Solution**: Check Deepseek API key and network connectivity

### Issue: Slow Vector Search

**Solution**: 
- Add database indexes
- Reduce similarity threshold
- Limit max results
- Consider using specialized vector database (Milvus, Qdrant)

### Issue: Memory Issues

**Solution**:
- Increase JVM heap size: `-Xmx2g`
- Optimize MyBatis result mapping
- Use pagination

## Support

For backend-specific questions:
- Check logs in `logs/` directory
- Review Swagger documentation
- Check existing issues on GitHub
- Consult Spring Boot documentation
