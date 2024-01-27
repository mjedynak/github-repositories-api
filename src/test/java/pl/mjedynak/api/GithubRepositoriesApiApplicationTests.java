package pl.mjedynak.api;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.mjedynak.api.domain.model.Branch;
import pl.mjedynak.api.domain.model.Repository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock({@ConfigureWireMock(name = "github-service", property = "github.api.url")})
class GithubRepositoriesApiApplicationTests {

    private static final String NEXT_PAGE_HEADER = "rel=\"next\"";

    private static final String SINGLE_REPOSITORY =
            """
            [
              {
                "name": "Hello-World",
                "owner": {
                  "login": "mjedynak"
                },
                "fork": false
              }
            ]
            """;
    private static final String ANOTHER_REPOSITORY =
            """
            [
              {
                "name": "Hello-World2",
                "owner": {
                  "login": "mjedynak"
                },
                "fork": false
              }
            ]
            """;
    private static final String TWO_REPOSITORIES_WITH_ONE_FORK =
            """
            [
              {
                "name": "Hello-World",
                "owner": {
                  "login": "mjedynak"
                },
                "fork": false
              },
              {
                "name": "Hello-World2",
                "owner": {
                  "login": "mjedynak"
                },
                "fork": true
              }
            ]
            """;
    private static final String SINGLE_BRANCH =
            """
            [
              {
            	"name": "master",
            	"commit": {
            	  "sha": "c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc"
            	}
              }
            ]
            """;
    private static final String SECOND_BRANCH =
            """
            [
              {
            	"name": "anotherBranch",
            	"commit": {
            	  "sha": "d01060d65ede31cc077d5a4445b91654740b86b5"
            	}
              }
            ]
            """;
    private static final String THIRD_BRANCH =
            """
            [
              {
            	"name": "master",
            	"commit": {
            	  "sha": "265303331d83bff373961a0e88f659a593d07ef8"
            	}
              }
            ]
            """;

    @InjectWireMock("github-service")
    private WireMockServer wiremock;

    @Autowired
    private WebTestClient webClient;

    @Test
    void returnsRepositoriesWhichAreNotForks() {
        wiremock.stubFor((get("/users/mjedynak/repos?per_page=100&page=1")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(TWO_REPOSITORIES_WITH_ONE_FORK))));
        wiremock.stubFor((get("/repos/mjedynak/Hello-World/branches?per_page=100&page=1")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(SINGLE_BRANCH))));

        webClient
                .get()
                .uri("/repositories/{user}", "mjedynak")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Repository.class)
                .hasSize(1)
                .contains(new Repository(
                        "Hello-World",
                        "mjedynak",
                        List.of(new Branch("master", "c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc"))));
    }

    @Test
    void returnsAllRepositoriesOverMultiplePages() {
        wiremock.stubFor((get("/users/mjedynak/repos?per_page=100&page=1")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Link", NEXT_PAGE_HEADER)
                        .withBody(SINGLE_REPOSITORY))));
        wiremock.stubFor((get("/users/mjedynak/repos?per_page=100&page=2")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(ANOTHER_REPOSITORY))));
        wiremock.stubFor((get("/repos/mjedynak/Hello-World/branches?per_page=100&page=1")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withHeader("Link", "rel=\"next\"")
                        .withBody(SINGLE_BRANCH))));
        wiremock.stubFor((get("/repos/mjedynak/Hello-World/branches?per_page=100&page=2")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(SECOND_BRANCH))));
        wiremock.stubFor((get("/repos/mjedynak/Hello-World2/branches?per_page=100&page=1")
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(THIRD_BRANCH))));

        webClient
                .get()
                .uri("/repositories/{user}", "mjedynak")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Repository.class)
                .hasSize(2)
                .contains(
                        new Repository(
                                "Hello-World",
                                "mjedynak",
                                List.of(
                                        new Branch("master", "c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc"),
                                        new Branch("anotherBranch", "d01060d65ede31cc077d5a4445b91654740b86b5"))),
                        new Repository(
                                "Hello-World2",
                                "mjedynak",
                                List.of(new Branch("master", "265303331d83bff373961a0e88f659a593d07ef8"))));
    }

    @Test
    void returns404WhenCannotFindRepositoriesForUser() {
        webClient
                .get()
                .uri("/repositories/{user}", "notExistingUser")
                .header(HttpHeaders.ACCEPT, "application/json")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void returns406WhenClientExpectsNotSupportedResponseFormat() {
        webClient
                .get()
                .uri("/repositories/{user}", "anyUser")
                .header(HttpHeaders.ACCEPT, "application/xml")
                .exchange()
                .expectStatus()
                .isEqualTo(NOT_ACCEPTABLE);
    }
}
