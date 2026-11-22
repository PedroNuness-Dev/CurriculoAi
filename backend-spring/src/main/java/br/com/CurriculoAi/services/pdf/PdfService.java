package br.com.CurriculoAi.services.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;

@Service
public class PdfService {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().escapeHtml(true).sanitizeUrls(true).build();
    private final Logger logger = Logger.getLogger(PdfService.class.getName());

    public byte[] generatePdfFromMarkdown(String markdown) throws Exception {

        logger.info("Iniciando a geração do PDF");

        if (markdown == null || markdown.isBlank()) throw new IllegalArgumentException("Currículo vazio.");
        Node document = parser.parse(markdown);
        String htmlContent = renderer.render(document);
        return renderPdf(htmlContent);
    }

    public byte[] generatePdfFromText(String text) throws Exception {
        var lines = text.strip().lines().filter(line -> !line.isBlank()).iterator();
        StringBuilder html = new StringBuilder();
        boolean title = true;
        while (lines.hasNext()) {
            String tag = title ? "h1" : "p";
            html.append("<").append(tag).append(">")
                    .append(org.springframework.web.util.HtmlUtils.htmlEscape(lines.next(), "UTF-8"))
                    .append("</").append(tag).append(">");
            title = false;
        }
        return renderPdf(html.toString());
    }

    private byte[] renderPdf(String htmlContent) throws Exception {

        // Template HTML completo
        String finalHtml = """
                <html lang="pt-BR">
                    <head>
                        <meta charset="UTF-8" />
                        <style>
                            @page { size: A4; margin: 17mm 18mm 18mm; }
                            body { margin: 0; font-family: Helvetica, Arial, sans-serif;
                                   font-size: 10.5pt; line-height: 1.45; color: #202020; }
                            h1, h2, h3 { page-break-after: avoid; }
                            h1 { margin: 0 0 4pt; font-size: 27pt; line-height: 1.12; color: #151515; }
                            h1 + p, h1 + p + p { font-size: 9.5pt; color: #515762; }
                            h1 + p strong { font-size: 14pt; color: #202020; }
                            h2 { margin: 17pt 0 6pt; padding-bottom: 3pt; font-size: 11.5pt;
                                 text-transform: uppercase; color: #142d50; border-bottom: 0.7pt solid #b8bcc3; }
                            h3 { margin: 8pt 0 2pt; font-size: 11pt; }
                            h3 + p { font-size: 9.5pt; color: #515762; page-break-after: avoid; }
                            p, li { orphans: 2; widows: 2; }
                            p { margin: 0 0 4pt; }
                            ul, ol { margin: 3pt 0 6pt; padding-left: 16pt; }
                            a { color: #142d50; text-decoration: none; }
                            hr, img { display: none; }
                        </style>
                    </head>

                    <body>
                        %s
                    </body>
                </html>
                """.formatted(htmlContent.replace('\u2011', '-').replace('\u2010', '-')
                        .replace('\u202f', ' ').replace('\u00a0', ' '));

        try (var output = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useExternalResourceAccessControl((uri, type) -> false,
                    com.openhtmltopdf.outputdevice.helper.ExternalResourceControlPriority.RUN_BEFORE_RESOLVING_URI);
            builder.withHtmlContent(finalHtml, null);
            builder.toStream(output);
            builder.run();
            return output.toByteArray();
        }
    }
}
