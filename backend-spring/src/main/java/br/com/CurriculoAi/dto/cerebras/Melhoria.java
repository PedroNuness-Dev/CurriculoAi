package br.com.CurriculoAi.dto.cerebras;
import java.util.stream.Stream;
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