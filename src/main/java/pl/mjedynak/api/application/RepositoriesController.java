package pl.mjedynak.api.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mjedynak.api.domain.RepositoriesService;
import pl.mjedynak.api.domain.model.Repository;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/repositories")
@AllArgsConstructor
@Slf4j
public class RepositoriesController {

    private final RepositoriesService repositoriesService;

    @GetMapping(value = "/{user}")
    public Flux<Repository> getUser(@PathVariable("user") String user) {
        log.info("Fetching repositories for {}", user);
        return repositoriesService.repositoriesFor(user);
    }
}
