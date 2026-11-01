package br.com.CurriculoAi.services.cerebras;

import br.com.CurriculoAi.dto.area.AreaResponse;
import br.com.CurriculoAi.dto.availability.AvailabilityResponse;
import br.com.CurriculoAi.dto.cerebras.CerebrasRequestDTO;
import br.com.CurriculoAi.dto.certification.CertificationResponse;
import br.com.CurriculoAi.dto.experience.ExperienceResponse;
import br.com.CurriculoAi.dto.formation.FormationResponse;
import br.com.CurriculoAi.dto.user.UserFullContent;
import br.com.CurriculoAi.dto.user.UserLanguageResponse;
import br.com.CurriculoAi.repositories.curriculum.CurriculumRepository;
import br.com.CurriculoAi.services.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CerebrasService {

    @Value("${cerebras.api.key}")
    private String apiKey;

    @Value("${cerebras.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;
    private final CurriculumRepository curriculumRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");

    public String gerarMarkdawn() throws Exception {

        UserFullContent userFullContent = userService.getUserFullContentData();

        if (userFullContent == null) {
            throw new IllegalArgumentException("Curriculo não pode ser nulo");
        }

        if (userFullContent.user() == null) {
            throw new IllegalArgumentException("Dados do usuario nao encontrados. Verifique o mapper.");
        }

        log.info("Iniciando geração de markdown");

        String prompt = montarPrompt(userFullContent);

        CerebrasRequestDTO requestDTO = new CerebrasRequestDTO(prompt);

        String bodyJson = objectMapper.writeValueAsString(requestDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<String> request = new HttpEntity<>(bodyJson, headers);

        log.info("Enviando request para Cerebras");

        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

        Map body = response.getBody();

        if (body == null || body.get("choices") == null) {
            throw new RuntimeException("Resposta inválida da API Cerebras");
        }

        List<Map> choices = (List<Map>) body.get("choices");
        Map message = (Map) choices.get(0).get("message");

        log.info("Resposta recebida com sucesso");

        return (String) message.get("content");
    }

    private String montarPrompt(UserFullContent userFullContent) {

        String prompt = """
                You are a senior technical recruiter and ATS resume optimization expert. Your only task is to transform the candidate data below into a professional, ATS-optimized resume in Markdown.

                Output the entire resume in Brazilian Portuguese. Exception: technology names, frameworks, tools, APIs, and technical acronyms remain in English (e.g., Docker, React, REST API, Scrum).

                ---

                ## ABSOLUTE RULES

                1. Use ONLY information explicitly provided. Never invent data, metrics, companies, or technologies.
                2. Output ONLY the resume. No introductions, explanations, comments, or closing remarks.
                3. If a field has no data, omit that section entirely.
                4. The first character of your output must be `#` followed by the candidate's name. Nothing before it.
                5. Never use ellipsis (...), placeholders, or code blocks (``` or similar).

                ---

                ## OUTPUT FORMAT

                Follow this exact structure. Omit any section with no data.

                # [Full Name]

                [City, State] | [Email] | [linkedin.com/in/username](https://linkedin.com/in/username) | [github.com/username](https://github.com/username)

                ---

                ## Resumo Profissional

                [3 to 4 lines: professional identity + stack + years of experience / main specialization and differentiator / key achievements / career objective if senior]

                ## Habilidades Técnicas

                ### Languages
                Python 3.10, Java 17, JavaScript

                ### Backend
                Django, Spring Boot, Node.js

                [Use only subcategories the candidate explicitly mentioned. Never use bullet points for skills — comma-separated on the line below each subcategory label.]

                ## Experiência Profissional

                ### [Company] — [Area]
                **[Job Title]** | [MM/YYYY] – [MM/YYYY or Atual]

                - [Action verb + technology + result]
                - [Action verb + technology + result]

                ## Educação

                ### [Institution] — [Area]
                **[Degree type] em [Field]** | [MM/YYYY] – [MM/YYYY or Em andamento]

                ## Certificações

                - **[Institution name]** | [MM/YYYY]

                ## Idiomas

                - **[Language]:** [Nativo / Fluente (C1/C2) / Avançado (B2) / Intermediário (B1) / Básico (A2)]

                ---

                ## WRITING RULES

                - Start every experience bullet with a strong action verb: Designed, Implemented, Developed, Optimized, Migrated, Reduced, Led, Automated, Integrated, Architected, Delivered, Launched, Refactored, Deployed, Built, Scaled, Modernized.
                - Vary bullet structure across and within roles. Never repeat the same sentence pattern.
                - Include metrics when the candidate provides them. Never invent numbers.
                - Mention specific technologies in at least 60% of experience bullets.
                - Avoid: "responsible for", "assisted with", "helped the team", vague adjectives with no technical backing.

                ---

                ## CANDIDATE DATA

                Full Name: {name}
                Email: {email}
                Area of Expertise: {area}

                Professional Experience:
                {experience}

                Education:
                {education}

                Certifications:
                {certifications}

                Languages:
                {languages}

                Work Availability: {disponibilidade}
                """;

        return prompt
                .replace("{name}",         safe(userFullContent.user().name()))
                .replace("{email}",        safe(userFullContent.user().email()))
                .replace("{area}",         formatarArea(userFullContent.area()))
                .replace("{experience}",   formatarExperiencias(userFullContent.experiences()))
                .replace("{education}",    formatarFormacoes(userFullContent.formations()))
                .replace("{certifications}", formatarCertificacoes(userFullContent.certifications()))
                .replace("{languages}",    formatarIdiomas(userFullContent.languages()))
                .replace("{disponibilidade}", formatarDisponibilidade(userFullContent.availability()));
    }

    private String safe(String value) {
        return value != null ? value : "";
    }

    private String formatarArea(AreaResponse area) {
        if (area == null || area.name() == null) return "Não informado";
        return area.name();
    }

    private String formatarExperiencias(List<ExperienceResponse> experiencias) {
        if (experiencias == null || experiencias.isEmpty()) return "Não informado.";

        log.info("Formatando experiências");

        StringBuilder sb = new StringBuilder();

        for (ExperienceResponse e : experiencias) {

            String empresa = (e.enterprise() != null) ? safe(e.enterprise().name()) : "Empresa não informada";
            String area    = (e.area() != null)    ? safe(e.area().name()) : "";
            String cargo   = safe(e.position());
            String inicio  = (e.startDate() != null) ? e.startDate().format(FORMATTER) : "?";
            String fim     = Boolean.TRUE.equals(e.workCurrent())
                    ? "Atual"
                    : (e.finishDate() != null ? e.finishDate().format(FORMATTER) : "?");
            String descricao = safe(e.description());

            sb.append("Company: ").append(empresa).append("\n");
            if (!area.isBlank()) sb.append("Area: ").append(area).append("\n");
            sb.append("Role: ").append(cargo).append("\n");
            sb.append("Period: ").append(inicio).append(" – ").append(fim).append("\n");
            if (!descricao.isBlank()) sb.append("Description: ").append(descricao).append("\n");
            sb.append("\n");
        }

        return sb.toString().trim();
    }

    private String formatarFormacoes(List<FormationResponse> formacoes) {
        if (formacoes == null || formacoes.isEmpty()) return "Não informado.";

        log.info("Formatando formações");

        StringBuilder sb = new StringBuilder();

        for (FormationResponse f : formacoes) {
            if (f == null) continue;

            String tipo  = safe(f.formationType().name());

            // A record FormacaoUserDtoResponse tem campo 'curso' (CursoDtoResponse)
            // e CursoDtoResponse expõe o accessor 'nomeCurso()'
            String cursoNome = "Curso não informado";
            if (f.course() != null && f.course().name() != null && !f.course().name().isBlank()) {
                cursoNome = f.course().name();
            }

            String inicio = (f.startDate() != null) ? f.startDate().format(FORMATTER) : "?";
            String fim = Boolean.TRUE.equals(f.inProgress())
                    ? "Em andamento"
                    : (f.finishDate() != null ? f.finishDate().format(FORMATTER) : "?");

            sb.append("Type: ").append(tipo).append("\n");
            sb.append("Field: ").append(cursoNome).append("\n");
            sb.append("Period: ").append(inicio).append(" – ").append(fim).append("\n");
            sb.append("\n");
        }

        return sb.toString().trim();
    }

    private String formatarCertificacoes(List<CertificationResponse> certificacoes) {
        if (certificacoes == null || certificacoes.isEmpty()) return "Não informado.";

        log.info("Formatando certificações");

        StringBuilder sb = new StringBuilder();

        for (CertificationResponse c : certificacoes) {

            String instituicao = (c.institution() != null) ? safe(c.institution().name()) : "Instituição não informada";
            String data = Boolean.TRUE.equals(c.inProgress())
                    ? "Em andamento"
                    : (c.finishDate() != null ? c.finishDate().format(FORMATTER) : "?");

            sb.append("- ").append(instituicao).append(" | ").append(data).append("\n");
        }

        return sb.toString().trim();
    }

    private String formatarIdiomas(List<UserLanguageResponse> idiomas) {
        if (idiomas == null || idiomas.isEmpty()) return "Não informado.";

        log.info("Formatando idiomas");

        StringBuilder sb = new StringBuilder();

        for (UserLanguageResponse i : idiomas) {
            if (i == null) continue;

            // nome do idioma vem do record IdiomaDtoResponse.idiomaNome()
            String nomeIdioma = "Idioma não informado";
            if (i.language() != null && i.language().name().getDescricao() != null && !i.language().name().getDescricao().isBlank()) {
                nomeIdioma = i.language().name().getDescricao();
            }

            // nível vem do próprio IdiomaUserDtoResponse.nivel()
            String nivel = (i.level() != null && !i.level().getDescricao().isBlank()) ? i.level().getDescricao() : "Nível não informado";

            sb.append("- ").append(nomeIdioma).append(": ").append(nivel).append("\n");
        }

        return sb.toString().trim();
    }

    private String formatarDisponibilidade(AvailabilityResponse availabilityResponse) {
        if (availabilityResponse == null) return "Não informado.";

        StringBuilder sb = new StringBuilder();

        if (availabilityResponse.workModel() != null) {
            sb.append(availabilityResponse.workModel().toString());
        }

        if (availabilityResponse.startAvailability() != null) {
            sb.append(" a partir de ")
                    .append(availabilityResponse.startAvailability().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        return sb.toString().trim();
    }
}