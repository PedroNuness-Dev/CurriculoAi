package br.com.CurriculoAi.dto.cerebras;
import java.util.List;
public record JobCompatibility(String summary, List<String> matchedRequirements,
                                  List<String> requirementsWithoutEvidence, AssessmentScore score) {
        public JobCompatibility {
            if (score == null || summary == null || summary.isBlank() || CurriculumAnalysis.invalidLists(matchedRequirements, requirementsWithoutEvidence)) {
                throw new IllegalArgumentException("Comparação com a jobDescription incompleta");
            }
        }
    }