# DOCKER DOCUMENTATION

### Build Docker Image:
docker build -t featureflagservice .

### Run Docker Container
docker run -p 8080:8080 featureflagservice

### List Docker Images
docker images

### Stop Running Container
docker ps         # Get container ID
docker stop <container_id>

### Remove Docker Image
docker rmi featureflagservice

### Tag Image for Docker Hub
docker tag featureflagservice your-dockerhub-username/featureflagservice:latest

### Push Image to Docker Hub
docker push your-dockerhub-username/featureflagservice:latest

### Pull Image from Docker Hub
docker pull your-dockerhub-username/featureflagservice:latest

### Docker Compose Commands
docker-compose up --build

### Stop Compose Services
docker-compose down

### Test If It's Running
curl -H "x-api-key: my-secret-key" http://localhost:8080/flags/metrics


