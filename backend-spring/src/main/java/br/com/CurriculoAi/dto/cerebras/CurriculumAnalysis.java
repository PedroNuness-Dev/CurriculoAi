package br.com.CurriculoAi.dto.cerebras;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.stream.Stream;

public record CurriculumAnalysis(String summary, List<String> strengths,
                                 List<String> weaknesses, List<String> suggestions,
                                 @JsonInclude(JsonInclude.Include.NON_NULL) JobCompatibility compatibility,
                                 List<Improvement> improvements, AssessmentScore structureScore, AssessmentScore contentScore) {
    @SafeVarargs
    static boolean invalidLists(List<String>... lists) {
        return Stream.of(lists).anyMatch(list -> list == null
                || list.stream().anyMatch(item -> item == null || item.isBlank()));
    }
    public CurriculumAnalysis {
        if (structureScore == null || contentScore == null || summary == null || summary.isBlank() || improvements == null || improvements.stream().anyMatch(java.util.Objects::isNull)
                || invalidLists(strengths, weaknesses, suggestions)) {
            throw new IllegalArgumentException("AnÃ¡lise incompleta");
        }
        improvements = improvements.stream().sorted(java.util.Comparator.comparing(Improvement::priority)).toList();
    }
}
