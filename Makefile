BUILD_NUMBER := SNAPSHOT
RELEASE_VERSION := 1.0-${BUILD_NUMBER}

build:
	./mvnw clean build-helper:parse-version versions:set -DnewVersion=${RELEASE_VERSION} -DprocessAllModules -DgenerateBackupPoms=false
	./mvnw install spring-boot:build-image -Drelease.version=${RELEASE_VERSION}

push:
	docker images
	docker push docker.io/vcosqui/organization-app-kotlin:${RELEASE_VERSION}
