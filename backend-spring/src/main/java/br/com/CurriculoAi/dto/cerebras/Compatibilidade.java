package br.com.CurriculoAi.dto.cerebras;
import java.util.List;
public record Compatibilidade(String resumo, List<String> requisitosAtendidos,
                                  List<String> requisitosSemEvidencia) {
        public Compatibilidade {
            if (resumo == null || resumo.isBlank() || CurriculumAnalysis.invalidLists(requisitosAtendidos, requisitosSemEvidencia)) {
                throw new IllegalArgumentException("Comparação com a vaga incompleta");
            }
        }
    }