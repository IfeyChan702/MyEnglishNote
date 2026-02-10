# MyEnglishNote - Deployment Guide

This guide provides step-by-step instructions for deploying the MyEnglishNote application in various environments.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Local Development Setup](#local-development-setup)
- [Docker Deployment](#docker-deployment)
- [Production Deployment on Ubuntu Server](#production-deployment-on-ubuntu-server)
- [Environment Configuration](#environment-configuration)
- [Monitoring and Logs](#monitoring-and-logs)
- [Troubleshooting](#troubleshooting)

## Prerequisites

### Required Software

- **Docker**: 20.10+ and Docker Compose 2.0+
- **Java**: JDK 17 (for local development)
- **Maven**: 3.6+ (for local development)
- **MySQL**: 8.0+ (for local development)
- **Redis**: 6.0+ (optional, for caching)
- **Flutter**: 3.0+ (for mobile app development)

### API Keys

- **Deepseek API Key**: Required for RAG functionality
  - Get your API key from [Deepseek Platform](https://platform.deepseek.com)

## Local Development Setup

### 1. Database Setup

```bash
# Start MySQL
mysql -u root -p

# Create database
CREATE DATABASE `ry-vue` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Import schema
mysql -u root -p ry-vue < sql/ry_20250522.sql
mysql -u root -p ry-vue < sql/quartz.sql
mysql -u root -p ry-vue < sql/rag_init.sql
```

### 2. Configure Application

Edit `ruoyi-admin/src/main/resources/application-druid.yml`:

```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/ry-vue?useUnicode=true&characterEncoding=utf8
        username: root
        password: your-password
```

Edit `ruoyi-admin/src/main/resources/application-rag.yml`:

```yaml
deepseek:
  api:
    key: your-deepseek-api-key
    base-url: https://api.deepseek.com
```

### 3. Build and Run Backend

```bash
# Build the project
mvn clean package -DskipTests

# Run the application
cd ruoyi-admin/target
java -jar ruoyi-admin.jar
```

The backend will be available at `http://localhost:9501`

### 4. Run Flutter App

```bash
cd flutter_app

# Install dependencies
flutter pub get

# Generate code
flutter pub run build_runner build --delete-conflicting-outputs

# Run on desired platform
flutter run                    # Default device
flutter run -d chrome         # Web
flutter run -d windows        # Windows
```

## Docker Deployment

Docker deployment is the recommended approach for both development and production.

### 1. Prepare Environment File

```bash
# Copy the example environment file
cp .env.example .env

# Edit .env with your values
nano .env
```

Required environment variables:

```env
MYSQL_ROOT_PASSWORD=your-secure-root-password
MYSQL_DATABASE=ry-vue
MYSQL_USER=appuser
MYSQL_PASSWORD=your-secure-app-password
DEEPSEEK_API_KEY=your-deepseek-api-key
```

### 2. Start All Services

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Check service status
docker-compose ps
```

### 3. Verify Deployment

```bash
# Check backend health
curl http://localhost:9501/actuator/health

# Check MySQL connection
docker exec -it myenglishnote-mysql mysql -u root -p

# Check Redis
docker exec -it myenglishnote-redis redis-cli ping
```

### 4. Stop Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (WARNING: deletes data)
docker-compose down -v
```

## Production Deployment on Ubuntu Server

### 1. Server Preparation

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Install Git
sudo apt install git -y
```

### 2. Clone Repository

```bash
# Clone the repository
git clone https://github.com/IfeyChan702/MyEnglishNote.git
cd MyEnglishNote
```

### 3. Configure for Production

```bash
# Create environment file
cp .env.example .env
nano .env
```

Production `.env` example:

```env
MYSQL_ROOT_PASSWORD=VerySecureRootPassword123!
MYSQL_DATABASE=ry-vue
MYSQL_USER=appuser
MYSQL_PASSWORD=SecureAppPassword456!
DEEPSEEK_API_KEY=sk-your-actual-deepseek-api-key
SPRING_PROFILES_ACTIVE=prod
```

### 4. Configure Nginx (Reverse Proxy)

```bash
# Install Nginx
sudo apt install nginx -y

# Create Nginx configuration
sudo nano /etc/nginx/sites-available/myenglishnote
```

Nginx configuration:

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # Backend API
    location /api/ {
        proxy_pass http://localhost:9501/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Other backend endpoints
    location / {
        proxy_pass http://localhost:9501/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

Enable the site:

```bash
sudo ln -s /etc/nginx/sites-available/myenglishnote /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### 5. Setup SSL with Let's Encrypt

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Obtain SSL certificate
sudo certbot --nginx -d your-domain.com

# Auto-renewal is configured automatically
```

### 6. Start Application

```bash
# Start services in production mode
docker-compose up -d

# Enable auto-start on boot
sudo systemctl enable docker
```

### 7. Setup Systemd Service (Alternative)

Create `/etc/systemd/system/myenglishnote.service`:

```ini
[Unit]
Description=MyEnglishNote Docker Compose
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/path/to/MyEnglishNote
ExecStart=/usr/local/bin/docker-compose up -d
ExecStop=/usr/local/bin/docker-compose down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
```

Enable and start:

```bash
sudo systemctl daemon-reload
sudo systemctl enable myenglishnote
sudo systemctl start myenglishnote
```

## Environment Configuration

### Application Profiles

The application supports multiple profiles:

- `dev`: Development (default)
- `prod`: Production
- `amazone`: Amazon-specific configuration

Set profile via environment variable:

```bash
SPRING_PROFILES_ACTIVE=prod
```

### Database Configuration

Configure in `application-druid.yml` or via environment variables:

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
```

### RAG Configuration

Configure Deepseek API in `application-rag.yml`:

```yaml
deepseek:
  api:
    key: ${DEEPSEEK_API_KEY}
    base-url: ${DEEPSEEK_API_URL:https://api.deepseek.com}
    model:
      embedding: deepseek-embedding
      chat: deepseek-chat
```

## Monitoring and Logs

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f mysql

# Last 100 lines
docker-compose logs --tail=100 backend
```

### Monitor Resources

```bash
# Container stats
docker stats

# Disk usage
docker system df
```

### Health Checks

```bash
# Backend health
curl http://localhost:9501/actuator/health

# Detailed health (requires authentication)
curl http://localhost:9501/actuator/health/liveness
curl http://localhost:9501/actuator/health/readiness
```

### Database Backup

```bash
# Backup database
docker exec myenglishnote-mysql mysqldump -u root -p${MYSQL_ROOT_PASSWORD} ry-vue > backup_$(date +%Y%m%d).sql

# Restore database
docker exec -i myenglishnote-mysql mysql -u root -p${MYSQL_ROOT_PASSWORD} ry-vue < backup_20250210.sql
```

## Troubleshooting

### Backend Won't Start

1. Check logs: `docker-compose logs backend`
2. Verify database is running: `docker-compose ps mysql`
3. Check database connection:
   ```bash
   docker exec -it myenglishnote-mysql mysql -u root -p
   ```
4. Verify environment variables: `docker-compose config`

### Database Connection Errors

1. Check MySQL is healthy:
   ```bash
   docker-compose ps mysql
   docker-compose logs mysql
   ```

2. Verify credentials in `.env` file

3. Check network connectivity:
   ```bash
   docker network ls
   docker network inspect myenglishnote_myenglishnote-network
   ```

### Port Already in Use

```bash
# Find process using port
sudo lsof -i :9501
sudo lsof -i :3306

# Kill process
sudo kill -9 <PID>
```

### Out of Disk Space

```bash
# Clean unused Docker resources
docker system prune -a

# Remove old images
docker image prune -a

# Remove volumes (WARNING: deletes data)
docker volume prune
```

### Performance Issues

1. Increase Docker resources (Docker Desktop)
2. Adjust JVM memory in `docker-compose.yml`:
   ```yaml
   environment:
     JAVA_OPTS: "-Xms1024m -Xmx2048m"
   ```

3. Optimize MySQL:
   ```yaml
   command:
     - --max_connections=500
     - --innodb_buffer_pool_size=512M
   ```

### Flutter App Can't Connect

1. Update `lib/utils/constants.dart` with correct URL:
   - Android Emulator: `http://10.0.2.2:9501`
   - iOS Simulator: `http://localhost:9501`
   - Physical Device: `http://YOUR_SERVER_IP:9501`

2. Check firewall settings:
   ```bash
   sudo ufw allow 9501
   ```

3. Verify backend is accessible:
   ```bash
   curl http://localhost:9501/api/note/list
   ```

## Security Recommendations

1. **Use Strong Passwords**: Change all default passwords
2. **Enable Firewall**: Only open required ports
3. **Use HTTPS**: Always use SSL in production
4. **Regular Updates**: Keep Docker, OS, and dependencies updated
5. **Backup Data**: Regular database backups
6. **Environment Variables**: Never commit `.env` to version control
7. **API Keys**: Rotate API keys regularly
8. **Monitoring**: Set up monitoring and alerting

## Support

For issues and questions:
- GitHub Issues: https://github.com/IfeyChan702/MyEnglishNote/issues
- Documentation: See other docs in this repository
