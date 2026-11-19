package br.com.CurriculoAi.dto.cerebras;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.stream.Stream;

public record CurriculumAnalysis(String resumo, List<String> pontosFortes,
                                 List<String> pontosDeMelhoria, List<String> sugestoes,
                                 @JsonInclude(JsonInclude.Include.NON_NULL) Compatibilidade compatibilidade,
                                 List<Melhoria> melhorias) {
    public enum Prioridade { ALTA, MEDIA, BAIXA }

    public record Melhoria(Prioridade prioridade, String secao, String trechoOriginal, String problema,
                           String sugestao, String exemplo, String perguntaAoUsuario) {
        private static final java.util.regex.Pattern NUMERO =
                java.util.regex.Pattern.compile("\\d+(?:[.,]\\d+)*(?:\\s*%)?");

        public Melhoria verificarExemplo() {
            if (exemplo == null) return this;
            var numerosDaFonte = NUMERO.matcher(trechoOriginal).results()
                    .map(match -> match.group().replace(" ", "")).toList();
            if (NUMERO.matcher(exemplo).results().allMatch(match ->
                    numerosDaFonte.contains(match.group().replace(" ", "")))) return this;
            return new Melhoria(prioridade, secao, trechoOriginal, problema, sugestao, null, perguntaAoUsuario);
        }

        public Melhoria {
            if (prioridade == null || Stream.of(secao, trechoOriginal, problema, sugestao)
                    .anyMatch(value -> value == null || value.isBlank())) {
                throw new IllegalArgumentException("Melhoria incompleta");
            }
        }
    }
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
        if (resumo == null || resumo.isBlank() || melhorias == null || melhorias.stream().anyMatch(java.util.Objects::isNull)
                || invalidLists(pontosFortes, pontosDeMelhoria, sugestoes)) {
            throw new IllegalArgumentException("Análise incompleta");
        }
        melhorias = melhorias.stream().sorted(java.util.Comparator.comparing(Melhoria::prioridade)).toList();
    }
}
