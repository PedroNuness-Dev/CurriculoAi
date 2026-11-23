package br.com.CurriculoAi.dto.github;

import java.util.List;

public record GitHubPreview(String username, String name, String bio, String location, String profileUrl,
                            List<GitHubRepository> repositories, boolean hasMore, int page) {}
