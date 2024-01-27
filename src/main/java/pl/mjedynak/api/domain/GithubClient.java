package pl.mjedynak.api.domain;

import pl.mjedynak.api.domain.model.github.GithubBranch;
import pl.mjedynak.api.domain.model.github.GithubRepository;
import reactor.core.publisher.Flux;

public interface GithubClient {

    Flux<GithubRepository> fetchRepositories(String user);

    Flux<GithubBranch> fetchBranches(String owner, String repoName);
}
