package br.com.CurriculoAi.dto.github;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GitHubProjectSelection(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_.-]{1,100}") String repository,
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 10000) String description) {}
