package br.com.CurriculoAi.services.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
public class CurriculumTextExtractor {
    public String extract(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Envie o currículo em PDF no campo file.");
        }
        if (file.getSize() > 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "O PDF deve ter até 1 MiB.");
        }
        try (var input = file.getInputStream(); var document = PDDocument.load(input)) {
            if (document.isEncrypted()) {
                throw new IllegalArgumentException("Envie um PDF sem proteção.");
            }
            if (document.getNumberOfPages() > 20) {
                throw new IllegalArgumentException("O currículo deve ter até 20 páginas.");
            }
            String text = new PDFTextStripper().getText(document).strip();
            if (text.isBlank()) {
                throw new IllegalArgumentException("Envie um PDF com texto selecionável.");
            }
            if (text.length() > 40000) {
                throw new IllegalArgumentException("O texto do currículo deve ter até 40 mil caracteres.");
            }
            return text;
        } catch (IOException e) {
            throw new IllegalArgumentException("Envie um PDF válido e sem senha.");
        }
    }
}
