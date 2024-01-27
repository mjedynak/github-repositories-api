package pl.mjedynak.api.domain;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import pl.mjedynak.api.domain.model.Branch;
import pl.mjedynak.api.domain.model.Repository;
import reactor.core.publisher.Flux;

@AllArgsConstructor
@Service
public class RepositoriesDefaultService implements RepositoriesService {

    private final GithubClient client;

    @SneakyThrows
    public Flux<Repository> repositoriesFor(String user) {
        var repositories = client.fetchRepositories(user);
        return repositories.filter(githubRepo -> !githubRepo.isFork()).flatMap(githubRepo -> client.fetchBranches(
                        githubRepo.getOwnerLogin(), githubRepo.getName())
                .collectList()
                .map(githubBranches -> new Repository(
                        githubRepo.getName(),
                        githubRepo.getOwnerLogin(),
                        githubBranches.stream()
                                .map(b -> new Branch(b.getName(), b.getCommitSha()))
                                .toList())));
    }
}
