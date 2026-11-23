package br.com.CurriculoAi.services.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdfServiceTest {
    private final PdfService service = new PdfService();

    @Test
    void rendersSelectableTextAndClickableLinkOnA4() throws Exception {
        String markdown = "# Lívia Araújo\n\n**Desenvolvedora**\n\n[Portfólio](https://example.org/livia)\n\n## Formação\n\nCiência da Computação";
        try (var pdf = PDDocument.load(service.generatePdfFromMarkdown(markdown))) {
            assertEquals(1, pdf.getNumberOfPages());
            var page = pdf.getPage(0);
            assertEquals(595.3, page.getMediaBox().getWidth(), 1);
            assertEquals(841.9, page.getMediaBox().getHeight(), 1);
            assertTrue(new PDFTextStripper().getText(pdf).contains("Lívia Araújo"));
            assertTrue(page.getAnnotations().stream().anyMatch(a -> a instanceof PDAnnotationLink link
                    && link.getAction() instanceof PDActionURI uri && "https://example.org/livia".equals(uri.getURI())));
        }
    }

    @Test
    void paginatesLongExperienceWithoutDroppingLastEntry() throws Exception {
        String markdown = "# Perfil de teste\n\n## Experiência\n\n"
                + "### Desenvolvimento\n\n2021 a 2024\n\n- Manutenção do catálogo de produtos.\n\n".repeat(45)
                + "Registro final.";
        try (var pdf = PDDocument.load(service.generatePdfFromMarkdown(markdown))) {
            assertTrue(pdf.getNumberOfPages() > 1);
            assertTrue(new PDFTextStripper().getText(pdf).contains("Registro final."));
        }
    }
}
