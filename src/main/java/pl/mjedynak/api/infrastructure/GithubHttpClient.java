package pl.mjedynak.api.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.mjedynak.api.domain.GithubClient;
import pl.mjedynak.api.domain.exception.RepositoriesNotFoundException;
import pl.mjedynak.api.domain.model.github.GithubBranch;
import pl.mjedynak.api.domain.model.github.GithubRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GithubHttpClient implements GithubClient {

    private final WebClient webClient;

    public GithubHttpClient(@Value("${github.api.url}") String baseUrl, @Value("${github.api.token}") String token) {
        webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }

    public Flux<GithubRepository> fetchRepositories(String user) {
        return fetchRepositories(user, 1);
    }

    public Flux<GithubBranch> fetchBranches(String owner, String repoName) {
        return fetchBranches(owner, repoName, 1);
    }

    private Flux<GithubRepository> fetchRepositories(String user, int page) {
        return webClient
                .get()
                .uri("/users/{username}/repos?per_page=100&page={page}", user, page)
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.value() == HttpStatus.NOT_FOUND.value(),
                        error -> Mono.error(new RepositoriesNotFoundException(
                                "Cannot find repositories for user %s".formatted(user))))
                .toEntityList(GithubRepository.class)
                .flatMapMany(responseEntity -> {
                    var repositories = responseEntity.getBody();
                    if (repositories == null || repositories.isEmpty()) {
                        return Flux.empty();
                    } else {
                        var hasMorePages = hasLinkForNextPage(responseEntity.getHeaders());
                        if (hasMorePages) {
                            return Flux.concat(Flux.fromIterable(repositories), fetchRepositories(user, page + 1));
                        } else {
                            return Flux.fromIterable(repositories);
                        }
                    }
                });
    }

    private Flux<GithubBranch> fetchBranches(String owner, String repoName, int page) {
        return webClient
                .get()
                .uri("repos/{owner}/{repoName}/branches?per_page=100&page={page}", owner, repoName, page)
                .retrieve()
                .toEntityList(GithubBranch.class)
                .flatMapMany(responseEntity -> {
                    var branches = responseEntity.getBody();
                    if (branches == null || branches.isEmpty()) {
                        return Flux.empty();
                    } else {
                        var hasMorePages = hasLinkForNextPage(responseEntity.getHeaders());
                        if (hasMorePages) {
                            return Flux.concat(Flux.fromIterable(branches), fetchBranches(owner, repoName, page + 1));
                        } else {
                            return Flux.fromIterable(branches);
                        }
                    }
                });
    }

    private static boolean hasLinkForNextPage(HttpHeaders headers) {
        var linkHeaders = headers.get("Link");
        return linkHeaders != null && linkHeaders.stream().anyMatch(link -> link.contains("rel=\"next\""));
    }
}
