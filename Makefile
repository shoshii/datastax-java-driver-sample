VERSION=v1
PROJECT_ID?=shoshii
PROJECT=shoshii0102.synology.me:4443/${PROJECT_ID}
REPONAME=datastax-java-driver-sample

all: build

container:
	@echo "Building ${PROJECT}/${REPONAME}:${VERSION}"
	docker build --pull -t ${PROJECT}/${REPONAME}:${VERSION} .

build: container

push: build
	docker -- push ${PROJECT}/${REPONAME}:${VERSION}

test: test
	docker run -it ${PROJECT}/${REPONAME}:${VERSION} /bin/bash

.PHONY: all build test push