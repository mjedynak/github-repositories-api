package pl.mjedynak.api.domain.model;

import lombok.Builder;

@Builder
public record Branch(String name, String commitSha) {}
