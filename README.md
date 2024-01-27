# GitHub Repositories API![build](https://github.com/mjedynak/github-repositories-api/blob/master/.github/workflows/build-gradle-project.yml/badge.svg?branch=master)
Simple facade over official GitHub API that allows to retrieve information about given user's repositories.

The purpose of the project is to show sample implementation of REST API (implemented with Java, [Spring Boot](https://spring.io/projects/spring-boot), [WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html), [WireMock](https://wiremock.org/)) 

Project follows Hexagonal architecture tested with [ArchUnit](https://www.archunit.org/) (sample implementation based on [this](https://www.baeldung.com/hexagonal-architecture-ddd-spring) article).

## OpenAPI
**OpenAPI** documentation describing the details of functionality is available [here](src/main/resources/static/api-docs.yaml).

## Running the application
In order to start the application, you need to set your GitHub API token in environment variable '_GITHUB_API_TOKEN_'.


## Known limitations
For production readiness few features are missing:
- security
- retries
- handling more HTTP status codes (e.g. authorization error)