# MyEnglishNote - 智能英语学习笔记系统

A comprehensive English learning note-taking application with RAG (Retrieval-Augmented Generation) search and SRS (Spaced Repetition System) review capabilities.

## 🌟 Features

### Core Functionality
- **📝 Note Management**: Create, read, update, and delete English learning notes
- **🔍 RAG Search**: AI-powered semantic search using vector embeddings (Deepseek API)
- **🧠 SRS Review**: Intelligent spaced repetition system based on SuperMemo 2 algorithm
- **🔐 User Authentication**: Secure JWT-based authentication
- **📱 Cross-Platform**: Flutter app supports Android, iOS, Web, Windows, macOS, and Linux

### Advanced Features
- **Vector Embeddings**: Automatic generation and storage of 1024-dimensional embeddings
- **Similarity Search**: Cosine similarity-based note retrieval
- **AI-Powered Answers**: Context-aware responses from Deepseek Chat API
- **Smart Review Scheduling**: Adaptive learning intervals based on review performance
- **Tagging System**: Organize notes with custom tags
- **Pagination**: Efficient data loading for large note collections

## 🚀 Quick Start

### Prerequisites

- Docker and Docker Compose (recommended)
- OR: Java 17, Maven, MySQL 8.0, Redis, Flutter SDK

### Docker Deployment (Recommended)

```bash
# 1. Clone repository
git clone https://github.com/IfeyChan702/MyEnglishNote.git
cd MyEnglishNote

# 2. Configure environment
cp .env.example .env
nano .env  # Add your Deepseek API key and other settings

# 3. Start all services
docker-compose up -d

# 4. Verify services
docker-compose ps
curl http://localhost:9501/actuator/health
```

The backend will be available at `http://localhost:9501`

## 📚 Documentation

Comprehensive documentation is available in the following files:

- **[DEPLOYMENT.md](DEPLOYMENT.md)** - Deployment guide for local, Docker, and production
- **[BACKEND.md](BACKEND.md)** - Backend architecture and development guide
- **[FLUTTER.md](FLUTTER.md)** - Flutter app development and platform configuration
- **[API.md](API.md)** - Complete REST API reference with examples

## 🛠️ Technology Stack

### Backend
- Spring Boot 2.5.15, Spring Security + JWT, MySQL 8.0+, Redis, MyBatis, Swagger 3.0

### Frontend
- Flutter 3.0+, Dart, Provider/Riverpod, Dio, Material Design 3

### AI/ML
- Deepseek Embedding Model (1024-dim), Deepseek Chat Model, MySQL JSON + Cosine Similarity

### DevOps
- Docker + Docker Compose, Nginx, Let's Encrypt, Spring Boot Actuator

## 📧 Contact

- **Project**: [https://github.com/IfeyChan702/MyEnglishNote](https://github.com/IfeyChan702/MyEnglishNote)
- **Issues**: [https://github.com/IfeyChan702/MyEnglishNote/issues](https://github.com/IfeyChan702/MyEnglishNote/issues)

---

**Built with ❤️ for English learners**
