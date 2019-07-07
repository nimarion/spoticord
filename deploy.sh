
#!/bin/bash
docker build -t biospheere/spoticord .
docker login -u "$DOCKER_USERNAME" -p "$DOCKER_PASSWORD"
docker push biospheere/spoticord