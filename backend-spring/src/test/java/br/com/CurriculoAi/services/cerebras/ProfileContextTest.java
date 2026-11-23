package br.com.CurriculoAi.services.cerebras;

import br.com.CurriculoAi.dto.project.ProjectResponse;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ProfileContextTest {
    @Test void compactsExactRepetitionAndKeepsLinks() {
        var p = new ProjectResponse(1L, "API", "Usei Java. Usei Java. Testei entradas.", "https://example.com/api", Set.of());
        var groups = ProfileContext.projects(List.of(p, p));
        assertEquals(1, groups.size());
        assertEquals("Usei Java. Testei entradas.", groups.getFirst().get("description"));
        assertTrue(groups.toString().contains(p.link()));
    }
    @Test void replacesInventedProjectDetailsWithSourceFacts() {
        var p = new ProjectResponse(1L, "API", "Implementei uma API Java. Testei entradas.", "https://example.com/api", Set.of());
        String draft = "# Ana\n\n### API\nhttps://example.com/api\n- Usei ferramenta inventada.\n\n## Idiomas\nPortuguês\n";
        String result = ProfileContext.groundProjectSections(draft, List.of(p));
        assertFalse(result.contains("inventada"));
        assertTrue(result.contains("Implementei uma API Java."));
        assertTrue(result.contains("## Idiomas"));
    }
}
