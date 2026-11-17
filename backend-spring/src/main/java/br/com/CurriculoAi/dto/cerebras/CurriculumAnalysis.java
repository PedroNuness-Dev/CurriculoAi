package br.com.CurriculoAi.dto.cerebras;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.stream.Stream;

public record CurriculumAnalysis(String resumo, List<String> pontosFortes,
                                 List<String> pontosDeMelhoria, List<String> sugestoes,
                                 @JsonInclude(JsonInclude.Include.NON_NULL) Compatibilidade compatibilidade) {
    public record Compatibilidade(String resumo, List<String> requisitosAtendidos,
                                  List<String> requisitosSemEvidencia) {
        public Compatibilidade {
            if (resumo == null || resumo.isBlank() || invalidLists(requisitosAtendidos, requisitosSemEvidencia)) {
                throw new IllegalArgumentException("Comparação com a vaga incompleta");
            }
        }
    }

    @SafeVarargs
    private static boolean invalidLists(List<String>... lists) {
        return Stream.of(lists).anyMatch(list -> list == null
                || list.stream().anyMatch(item -> item == null || item.isBlank()));
    }
    public CurriculumAnalysis {
        if (resumo == null || resumo.isBlank() || invalidLists(pontosFortes, pontosDeMelhoria, sugestoes)) {
            throw new IllegalArgumentException("Análise incompleta");
        }
    }
}
