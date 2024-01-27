package pl.mjedynak.api.domain.exception;

public class RepositoriesNotFoundException extends RuntimeException {

    public RepositoriesNotFoundException(String message) {
        super(message);
    }
}
