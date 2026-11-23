package br.com.CurriculoAi.services.cerebras;

import br.com.CurriculoAi.client.cerebras.CerebrasClient;
import br.com.CurriculoAi.dto.user.UserFullContent;
import br.com.CurriculoAi.services.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.test.util.ReflectionTestUtils;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/** Explicit opt-in; uses the fictional fixture created by scripts/validate_resume_live.py. */
@EnabledIfEnvironmentVariable(named = "RUN_LIVE_AI", matches = "true")
class ProfileProviderLiveTest {
    @Test void verifiesRealEvidenceExtraction() throws Exception {
        var mapper = new ObjectMapper();
        var fixture = Path.of("target/live-validation/profile.json");
        var users = mock(UserService.class);
        when(users.getUserFullContentData()).thenReturn(mapper.readValue(Files.readString(fixture), UserFullContent.class));
        String key = System.getenv("CEREBRAS_API_KEY");
        CerebrasClient client = request -> {
            try {
                Files.writeString(Path.of("target/live-validation/provider-request.json"), request);
                var response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create("https://api.cerebras.ai/v1/chat/completions"))
                        .header("Authorization", "Bearer " + key).header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(request)).build(), HttpResponse.BodyHandlers.ofString());
                Files.writeString(Path.of("target/live-validation/provider-response.json"), response.body());
                assertEquals(200, response.statusCode());
                return response.body();
            } catch (Exception e) { throw new IllegalStateException(e); }
        };
        var service = new CerebrasService(client, users, null, null);
        ReflectionTestUtils.setField(service, "model", "gpt-oss-120b");
        assertFalse(service.studyProfile().isEmpty());
    }
}
