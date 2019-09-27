.PHONY: clean build

# VERSION file is not needed for local development, In the CI/CD pipeline, a temporary VERSION file is written
# if you need a specific version, just override below
VERSION=$(shell cat ./VERSION 2>/dev/null || echo 0.0.0)

# This pulls the version of the SDK from the go.mod file. If the SDK is the only required module,
# it must first remove the word 'required' so the offset of $2 is the same if there are multiple required modules

MICROSERVICE=support-rulesengine
MAVEN_OPTIONS="-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true"
GIT_SHA=$(shell git rev-parse HEAD)

build:
	mvn package $(MAVEN_OPTIONS)

# NOTE: This is only used for local development. Jenkins CI does not use this make target
docker:
	docker build \
	    --build-arg http_proxy \
	    --build-arg https_proxy \
		-f Dockerfile \
		--label "git_sha=$(GIT_SHA)" \
		-t edgexfoundry/docker-support-rulesengine:$(GIT_SHA) \
		-t edgexfoundry/docker-support-rulesengine:$(VERSION) \
		-t edgexfoundry/docker-support-rulesengine:latest \
		.

test:
	mvn test $(MAVEN_OPTIONS)

clean:
	mvn clean $(MAVEN_OPTIONS)