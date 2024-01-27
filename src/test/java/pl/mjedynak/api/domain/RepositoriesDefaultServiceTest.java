package pl.mjedynak.api.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.mjedynak.api.domain.model.Branch;
import pl.mjedynak.api.domain.model.Repository;
import pl.mjedynak.api.domain.model.github.GithubBranch;
import pl.mjedynak.api.domain.model.github.GithubRepository;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class RepositoriesDefaultServiceTest {

    @Mock
    private GithubClient githubClient;

    @InjectMocks
    private RepositoriesDefaultService repositoriesService;

    @Test
    void createsRepositoriesBasedOnCallsFromGithubClient() {
        var user = "someUser";
        var repositoryName = "someRepositoryName";
        var commitSha = "d01060d65ede31cc077d5a4445b91654740b86b5";
        var branchName = "someBranchName";
        given(githubClient.fetchRepositories(user))
                .willReturn(Flux.just(GithubRepository.builder()
                        .name(repositoryName)
                        .ownerLogin(user)
                        .fork(false)
                        .build()));
        given(githubClient.fetchBranches(user, repositoryName))
                .willReturn(Flux.just(GithubBranch.builder()
                        .commitSha(commitSha)
                        .name(branchName)
                        .build()));

        var result = repositoriesService.repositoriesFor(user);

        assertThat(result.toStream().toList())
                .hasSize(1)
                .contains(Repository.builder()
                        .name(repositoryName)
                        .ownerLogin(user)
                        .branches(List.of(Branch.builder()
                                .commitSha(commitSha)
                                .name(branchName)
                                .build()))
                        .build());
    }

    @Test
    void filtersRepositoriesThatAreForks() {
        var user = "someUser";
        given(githubClient.fetchRepositories(user))
                .willReturn(Flux.just(GithubRepository.builder()
                        .name("someRepositoryName")
                        .ownerLogin(user)
                        .fork(true)
                        .build()));

        var result = repositoriesService.repositoriesFor(user);

        assertThat(result.toStream().toList()).isEmpty();
    }
}
