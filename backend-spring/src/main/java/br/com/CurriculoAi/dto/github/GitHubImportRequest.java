package br.com.CurriculoAi.dto.github;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record GitHubImportRequest(@NotBlank String username,
        @NotNull @Size(min = 1, max = 10) List<@NotNull @Valid GitHubProjectSelection> projects) {}
