package pl.mjedynak.api.domain.model;

import java.util.List;
import lombok.Builder;

@Builder
public record Repository(String name, String ownerLogin, List<Branch> branches) {}
