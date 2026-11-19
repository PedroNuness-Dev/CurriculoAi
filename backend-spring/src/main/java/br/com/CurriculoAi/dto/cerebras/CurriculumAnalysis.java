package br.com.CurriculoAi.dto.cerebras;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.stream.Stream;

public record CurriculumAnalysis(String resumo, List<String> pontosFortes,
                                 List<String> pontosDeMelhoria, List<String> sugestoes,
                                 @JsonInclude(JsonInclude.Include.NON_NULL) Compatibilidade compatibilidade,
                                 List<Melhoria> melhorias) {
    @SafeVarargs
    static boolean invalidLists(List<String>... lists) {
        return Stream.of(lists).anyMatch(list -> list == null
                || list.stream().anyMatch(item -> item == null || item.isBlank()));
    }
    public CurriculumAnalysis {
        if (resumo == null || resumo.isBlank() || melhorias == null || melhorias.stream().anyMatch(java.util.Objects::isNull)
                || invalidLists(pontosFortes, pontosDeMelhoria, sugestoes)) {
            throw new IllegalArgumentException("Análise incompleta");
        }
        melhorias = melhorias.stream().sorted(java.util.Comparator.comparing(Melhoria::prioridade)).toList();
    }
}
