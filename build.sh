#!/bin/bash
SERVICES=("api-gateway" "query-service" "command-service")
DOCKER_REPO="123"
K8S_DIR="k8s"
DOCKERFILE="DOCKERFILE"
MVN_FLAGS="-DskipTests"

for SERVICE in "${SERVICES[@]}"; do
	echo "Budowanie $SERVICE"
	mvn -f "$SERVICE/pom.xml" clean package $MVN_FLAGS

	JAR_FILE="$SERVICE/taget/$SERVICE.jar"
	if [[ ! -f "$JAR_FILE" ]]; then
		echo "Nie znaleziono JAR: $JAR_FILE"
		exit 1
	fi

	IMAGE_TAG="${DOCKER_REPO}/${SERVICE}:latest"
	docker build \
		--build-arg JAR_PATH=$JAR_FILE \
		-t "$IMAGE_TAG" \
		-f "$DOCKERFILE" .
	echo "Obraz $IMAGE_TAG zbudowany"

	docker push "$IMAGE_TAG"
	echo "Obraz zpushowany do docker huba"

	YAML_FILE="${K8S_DIR}/${SERVICE}-deployment.yaml"
	if [[ -f "$YAML_FILE" ]]; then
		echo "kubectl apply (${YAML_FILE})"
		kubectl apply -f "$YAML_FILE"

	echo "kubectl rollout restart deployment/${SERVICE}"
	kubectl rollout restart deployment "$SERVICE"
done

echo "Build aplikacji zakonczony"
