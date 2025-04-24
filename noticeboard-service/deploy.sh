SERVICE_NAME=noticeboard-service
JAR_NAME=${SERVICE_NAME}-0.0.1-SNAPSHOT.jar
BUILD_JAR=build/libs/$JAR_NAME
FRONTEND_DIST_DIR=dist

echo "🧹 1. Cleaning & building with Gradle"
cd backend
./gradlew clean build -x test

if [ ! -f "$BUILD_JAR" ]; then
  echo "❌ Build failed: $BUILD_JAR not found"
  exit 1
fi
cd ..

echo "🐳 2. Docker Compose: down"
docker-compose down

echo "🚀 3. Docker Compose: up"
docker-compose up -d

echo "✅ Deployment complete!"
