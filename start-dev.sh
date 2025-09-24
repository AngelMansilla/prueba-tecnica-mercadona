#!/bin/bash

if ! docker info > /dev/null 2>&1; then
    echo "Docker not running"
    exit 1
fi

cleanup() {
    docker-compose down
}
trap cleanup EXIT

docker-compose up -d postgres external-stores

until docker-compose exec postgres pg_isready -U mercadona_user -d mercadona_db > /dev/null 2>&1; do
    sleep 2
done

until curl -f http://localhost:8080/health > /dev/null 2>&1; do
    sleep 2
done

echo "Services ready:"
echo "PostgreSQL: localhost:5432"
echo "External API: localhost:8080"
echo ""
echo "Run app: mvn spring-boot:run"
echo "Stop: Ctrl+C"

wait
