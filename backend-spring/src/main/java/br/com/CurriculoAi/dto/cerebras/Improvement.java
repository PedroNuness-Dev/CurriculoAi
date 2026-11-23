package br.com.CurriculoAi.dto.cerebras;
import java.util.stream.Stream;
public record Improvement(Priority priority, String section, String originalExcerpt, String problem,
                           String suggestion, String example, String question) {
        private static final java.util.regex.Pattern NUMBER =
                java.util.regex.Pattern.compile("\\d+(?:[.,]\\d+)*(?:\\s*%)?");

        public Improvement validateExample() {
            if (example == null) return this;
            var inventedLevel = java.util.regex.Pattern.compile("(?iu)\\b(?:iniciante|intermedi[aá]ri[oa]|avan[cç]ad[oa]|b[aá]sic[oa]|fluente)\\b");
            if (inventedLevel.matcher(example).results().anyMatch(match ->
                    !originalExcerpt.toLowerCase(java.util.Locale.ROOT).contains(match.group().toLowerCase(java.util.Locale.ROOT))))
                return new Improvement(priority, section, originalExcerpt, problem, suggestion, null, question);
            var urls = java.util.regex.Pattern.compile("https?://[^\\s|]+|[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}");
            if (urls.matcher(example).results().anyMatch(match -> !originalExcerpt.contains(match.group())))
                return new Improvement(priority, section, originalExcerpt, problem, suggestion, null, question);
            var sourceNumbers = NUMBER.matcher(originalExcerpt).results()
                    .map(match -> match.group().replace(" ", "")).toList();
            if (NUMBER.matcher(example).results().allMatch(match ->
                    sourceNumbers.contains(match.group().replace(" ", "")))) return this;
            return new Improvement(priority, section, originalExcerpt, problem, suggestion, null, question);
        }

        public Improvement {
            if (priority == null || Stream.of(section, originalExcerpt, problem, suggestion)
                    .anyMatch(value -> value == null || value.isBlank())) {
                throw new IllegalArgumentException("Improvement incompleta");
            }
        }
    }
