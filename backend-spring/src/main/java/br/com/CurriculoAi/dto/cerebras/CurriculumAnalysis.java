package br.com.CurriculoAi.dto.cerebras;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.stream.Stream;

public record CurriculumAnalysis(String summary, List<String> strengths,
                                 List<String> weaknesses, List<String> suggestions,
                                 @JsonInclude(JsonInclude.Include.NON_NULL) JobCompatibility compatibility,
                                 List<Improvement> improvements) {
    @SafeVarargs
    static boolean invalidLists(List<String>... lists) {
        return Stream.of(lists).anyMatch(list -> list == null
                || list.stream().anyMatch(item -> item == null || item.isBlank()));
    }
    public CurriculumAnalysis {
        if (summary == null || summary.isBlank() || improvements == null || improvements.stream().anyMatch(java.util.Objects::isNull)
                || invalidLists(strengths, weaknesses, suggestions)) {
            throw new IllegalArgumentException("Análise incompleta");
        }
        improvements = improvements.stream().sorted(java.util.Comparator.comparing(Improvement::priority)).toList();
    }
}
