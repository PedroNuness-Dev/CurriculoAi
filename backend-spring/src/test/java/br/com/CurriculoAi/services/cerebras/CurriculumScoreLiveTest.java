package br.com.CurriculoAi.services.cerebras;

import br.com.CurriculoAi.client.cerebras.CerebrasClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/** Opt-in prompt evaluation with fictional data only. */
@EnabledIfEnvironmentVariable(named = "RUN_LIVE_AI", matches = "true")
class CurriculumScoreLiveTest {
    @Test
    void doesNotRequireMetricsPhoneOrProjectDatesForStrongResume() {
        String resume = """
                Ana Costa | Recife, PE | ana@example.org

                RESUMO PROFISSIONAL
                Desenvolvedora backend com experiência em APIs REST Java e relatórios PostgreSQL.

                EXPERIÊNCIA PROFISSIONAL
                Desenvolvedora - Empresa Exemplo | 01/2024 - atual
                - Implementou um endpoint Spring Boot para consultar pedidos e apresentar seu estado.
                - Criou consultas PostgreSQL usadas em relatórios internos de pedidos.

                PROJETOS
                Catálogo API
                - Desenvolveu uma API Java para cadastrar produtos e consultar itens por categoria.

                FORMAÇÃO
                Tecnólogo em Análise e Desenvolvimento de Sistemas | 2021 - 2023

                HABILIDADES TÉCNICAS
                Java, Spring Boot, PostgreSQL, SQL, Git
                """;
        CerebrasClient client = request -> {
            try {
                var response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(
                                URI.create("https://api.cerebras.ai/v1/chat/completions"))
                        .header("Authorization", "Bearer " + System.getenv("CEREBRAS_API_KEY"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(request)).build(),
                        HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                return response.body();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        };
        var service = new CerebrasService(client, null, null, null);
        ReflectionTestUtils.setField(service, "model", "gpt-oss-120b");
        var result = service.analyze(resume, null);
        assertTrue(result.structureScore().value() == null || result.structureScore().value() >= 90,
                () -> new ObjectMapper().valueToTree(result).toString());
        assertTrue(result.contentScore().value() == null || result.contentScore().value() >= 85,
                () -> new ObjectMapper().valueToTree(result).toString());
        String rationale = result.contentScore().rationale().toLowerCase();
        assertFalse(rationale.contains("falta de métricas"), rationale);
        assertFalse(rationale.contains("ausência de métricas"), rationale);
        assertTrue(result.weaknesses().stream().noneMatch(item -> item.toLowerCase().contains("métrica")));
        assertTrue(result.suggestions().stream().noneMatch(item -> item.toLowerCase().contains("métrica")));
    }
}
