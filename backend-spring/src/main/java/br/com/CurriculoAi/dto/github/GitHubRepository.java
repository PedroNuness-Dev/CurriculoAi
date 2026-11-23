package br.com.CurriculoAi.dto.github;

public record GitHubRepository(String name, String description, String url, String language,
                               boolean fork, boolean archived) {}
