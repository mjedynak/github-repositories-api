# GitHub Repositories API
Simple facade over official GitHub API that allows to retrieve information about given user's repositories.

The purpose of the project is to show sample implementation of REST API (implemented with Java, Spring Boot, WebFlux, WireMock) with Hexagonal architecture tested with ArchUnit (based on [this](https://www.baeldung.com/hexagonal-architecture-ddd-spring) article).

In order to start the application, you need to set your GitHub API token in environment variable 'GITHUB_API_TOKEN'.
 
## OpenAPI
**OpenAPI** documentation describing the details of functionality is available [here](src/main/resources/static/api-docs.yaml).

## Known limitations
For production readiness few features are missing:
- security
- retries
- handling more HTTP status codes (e.g. authorization error)