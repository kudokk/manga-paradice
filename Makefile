NAME               := compass-manager-api
REVISION           := $(shell git rev-parse --short HEAD)
ORIGIN             := $(shell git remote get-url origin | sed -e 's/^.*@//g')
LOCAL_TAGS         := local
DEVEL_TAGS         := devel
RELEASE_TAGS       := production 0.0.3 $(REVISION)
REGISTRY           := registry.dc.mangaka.jp
USER               := compass-web

.PHONY: dummy
dummy:
	@cat ci/README.md

.PHONY: docker-build
docker-build:
	ci/run.sh $@

.PHONY: docker-push
docker-push:
	ci/run.sh $@

.PHONY: docker-run
docker-run:
	ci/run.sh $@

.PHONY: docker-maven-start
docker-maven-start:
	ci/run.sh $@

.PHONY: docker-maven-stop
docker-maven-stop:
	ci/run.sh $@

.PHONY: unit-test
unit-test:
	GIT_BRANCH=$(GIT_BRANCH) ci/run.sh $@

.PHONY: ktlint
ktlint:
	GIT_BRANCH=$(GIT_BRANCH) ci/run.sh $@

local_build:
	DOCKER_BUILDKIT=1 \
	docker build \
		$(addprefix -t $(REGISTRY)/$(USER)/$(NAME):,$(LOCAL_TAGS)) .

devel_build:
	DOCKER_BUILDKIT=1 \
	docker build \
		$(addprefix -t $(REGISTRY)/$(USER)/$(NAME):,$(DEVEL_TAGS)) .

prod_build:
	DOCKER_BUILDKIT=1 \
	docker build \
		$(addprefix -t $(REGISTRY)/$(USER)/$(NAME):,$(RELEASE_TAGS)) .

devel_push:
	@for TAG in $(DEVEL_TAGS); do \
		docker push $(REGISTRY)/$(USER)/$(NAME):$$TAG; \
	done

prod_push:
	@for TAG in $(RELEASE_TAGS); do \
		docker push $(REGISTRY)/$(USER)/$(NAME):$$TAG; \
	done

devel_clean:
	@for TAG in $(DEVEL_TAGS); do \
		if [ -n "$$(docker images -q $(REGISTRY)/$(USER)/$(NAME):$$TAG)" ]; then \
			docker image rm $(REGISTRY)/$(USER)/$(NAME):$$TAG; \
		fi \
	done

prod_clean:
	@for TAG in $(RELEASE_TAGS); do \
		if [ -n "$$(docker images -q $(REGISTRY)/$(USER)/$(NAME):$$TAG)" ]; then \
			docker image rm $(REGISTRY)/$(USER)/$(NAME):$$TAG; \
		fi \
	done

