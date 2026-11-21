package br.com.CurriculoAi.services.cerebras;

import br.com.CurriculoAi.client.cerebras.CerebrasClient;
import br.com.CurriculoAi.dto.user.*;
import br.com.CurriculoAi.services.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileStudyTest {
    @Test
    void discardsEvidenceAbsentFromProfile() throws Exception {
        var client = mock(CerebrasClient.class);
        var users = mock(UserService.class);
        var profile = new UserFullContent(new UserResponse(1L, "Ana", "ana@example.com", Set.of()),
                null, null, null, null, List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
        when(users.getUserFullContentData()).thenReturn(profile);
        String content = """
                {"insights":[{"skill":"Liderança","evidence":"Liderei cinco pessoas",
                "interpretation":"Coordenação de equipe","question":"Qual foi sua contribuição?"}]}
                """;
        when(client.complete(anyString())).thenReturn(new ObjectMapper().writeValueAsString(
                Map.of("choices", List.of(Map.of("message", Map.of("content", content))))));
        var service = new CerebrasService(client, users, null);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "model", "test-model");
        assertTrue(service.studyProfile().isEmpty());
    }
    @Test
    void generationAcceptsMissingJobAndLimitsSupplementaryData() {
        assertDoesNotThrow(() -> new br.com.CurriculoAi.dto.cerebras.GenerationRequest(null, null));
        assertThrows(IllegalArgumentException.class,
                () -> new br.com.CurriculoAi.dto.cerebras.GenerationRequest("x".repeat(16001), null));
    }
}
