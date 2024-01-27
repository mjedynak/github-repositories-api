package pl.mjedynak.api.domain;

import pl.mjedynak.api.domain.model.Repository;
import reactor.core.publisher.Flux;

public interface RepositoriesService {

    Flux<Repository> repositoriesFor(String user);
}
