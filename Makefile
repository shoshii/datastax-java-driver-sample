VERSION=v1
PROJECT_ID?=shoshii
PROJECT=${DOCKER_REPO}/${PROJECT_ID}
REPONAME=datastax-java-driver-sample

all: build

container:
	@echo "Building ${PROJECT}/${REPONAME}:${VERSION}"
	docker build --pull -t ${PROJECT}/${REPONAME}:${VERSION} .

build: container

push: build
	docker -- push --network cassandra ${PROJECT}/${REPONAME}:${VERSION}

test: test
	docker run -it --network cassandra ${PROJECT}/${REPONAME}:${VERSION} /bin/bash

.PHONY: all build test push