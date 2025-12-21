#!/bin/bash

# ShoutX Setup Script
# Complete environment setup for PostgreSQL + Instagram OAuth

set -e

echo "🚀 ShoutX Setup Script"
echo "======================"
echo ""

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check prerequisites
echo "📋 Checking prerequisites..."

command -v java >/dev/null 2>&1 || { echo -e "${RED}❌ Java 17+ required but not installed${NC}"; exit 1; }
command -v mvn >/dev/null 2>&1 || { echo -e "${RED}❌ Maven required but not installed${NC}"; exit 1; }
command -v docker >/dev/null 2>&1 || { echo -e "${RED}❌ Docker required but not installed${NC}"; exit 1; }
command -v docker-compose >/dev/null 2>&1 || { echo -e "${RED}❌ Docker Compose required but not installed${NC}"; exit 1; }

echo -e "${GREEN}✅ All prerequisites installed${NC}"
echo ""

# Check .env file
if [ ! -f ".env" ]; then
    echo -e "${YELLOW}⚠️  .env file not found. Creating from template...${NC}"
    cp .env.example .env
    echo -e "${RED}❗ IMPORTANT: Update .env with your credentials before running!${NC}"
    exit 1
fi

echo -e "${GREEN}✅ .env file found${NC}"
echo ""

# Load environment variables
export $(grep -v '^#' .env | xargs)

# Validate critical environment variables
echo "🔍 Validating environment variables..."

if [ -z "$JWT_SECRET" ] || [ ${#JWT_SECRET} -lt 32 ]; then
    echo -e "${RED}❌ JWT_SECRET must be at least 32 characters${NC}"
    exit 1
fi

if [ -z "$INSTAGRAM_CLIENT_ID" ]; then
    echo -e "${YELLOW}⚠️  INSTAGRAM_CLIENT_ID not set${NC}"
fi

echo -e "${GREEN}✅ Environment variables valid${NC}"
echo ""

# Start PostgreSQL
echo "🐘 Starting PostgreSQL database..."
docker-compose up -d shout-postgres

# Wait for PostgreSQL to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
sleep 5

until docker exec shout-postgres pg_isready -U shoutuser -d shoutdb > /dev/null 2>&1; do
    echo "   Still waiting..."
    sleep 2
done

echo -e "${GREEN}✅ PostgreSQL is ready${NC}"
echo ""

# Start Redis
echo "🔴 Starting Redis cache..."
docker-compose up -d shout-redis
sleep 2
echo -e "${GREEN}✅ Redis is ready${NC}"
echo ""

# Build backend
echo "🏗️  Building Spring Boot application..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Build successful${NC}"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

echo ""
echo "================================================"
echo -e "${GREEN}✅ Setup Complete!${NC}"
echo "================================================"
echo ""
echo "📝 Next Steps:"
echo "   1. Update .env with Instagram OAuth credentials"
echo "   2. Run: mvn spring-boot:run"
echo "   3. Access: http://localhost:8080"
echo ""
echo "🔗 Useful Commands:"
echo "   • Start: mvn spring-boot:run"
echo "   • Stop: Ctrl+C"
echo "   • Logs: docker-compose logs -f"
echo "   • DB: psql -U shoutuser -d shoutdb -h localhost"
echo ""
echo "📚 Documentation: README.md"
echo ""
