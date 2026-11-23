package br.com.CurriculoAi.config;

import br.com.CurriculoAi.exceptions.ErrorResponse;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static br.com.CurriculoAi.config.OpenApiResponseRefs.*;

/**
 * Configuração global da documentação OpenAPI (Swagger).
 * <p>
 * Define as informações da API, o esquema de autenticação JWT aplicado por padrão a todos os
 * endpoints e as respostas de erro reutilizáveis referenciadas pelos controllers via
 * {@link OpenApiResponseRefs}.
 */
@Configuration
public class OpenApiConfiguration {

    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    private static final String ERROR_SCHEMA_NAME = "ErrorResponse";
    private static final String ERROR_SCHEMA_REF = "#/components/schemas/" + ERROR_SCHEMA_NAME;

    @Bean
    public OpenAPI openAPI(){
        Components components = new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, bearerSecurityScheme());

        registerErrorSchema(components);
        registerErrorResponses(components);

        return new OpenAPI()
                .info(new Info()
                        .title("CurriculoAI API")
                        .description("""
                                API do CurriculoAI: cadastro do perfil profissional do candidato e geração \
                                de currículo com IA.

                                **Autenticação:** faça login em `POST /api/auth/login`, copie o `token` retornado \
                                e clique em **Authorize** para enviá-lo como `Bearer` nas demais requisições.

                                **Perfis de acesso:**
                                - `CANDIDATO`: gerencia os próprios dados (endpoints com o prefixo *Candidato*).
                                - `ADMIN`: mantém os cadastros da aplicação (endpoints com o prefixo *Catálogo*).
                                """)
                        .version("v1.0")
                        .contact(new Contact().name("CurriculoAI Suporte").email("suporte.curriculoai@gmail.com"))
                )
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(components);
    }

    private SecurityScheme bearerSecurityScheme(){
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Token JWT obtido em `POST /api/auth/login`");
    }

    private void registerErrorSchema(Components components){
        ModelConverters.getInstance()
                .readAll(new AnnotatedType(ErrorResponse.class))
                .forEach(components::addSchemas);
    }

    private void registerErrorResponses(Components components){
        components
                .addResponses(BAD_REQUEST_NAME, errorResponse(
                        "Requisição inválida: erro de validação nos campos ou regra de negócio violada"))
                .addResponses(UNAUTHORIZED_NAME, errorResponse(
                        "Não autenticado: token JWT ausente, inválido ou expirado"))
                .addResponses(FORBIDDEN_NAME, errorResponse(
                        "Acesso negado: o usuário autenticado não possui o perfil necessário"))
                .addResponses(NOT_FOUND_NAME, errorResponse(
                        "Recurso não encontrado ou não pertence ao usuário autenticado"))
                .addResponses(BAD_GATEWAY_NAME, errorResponse(
                        "Falha no serviço externo consultado pela API"))
                .addResponses(SERVICE_UNAVAILABLE_NAME, errorResponse(
                        "Serviço externo indisponível no momento. Tente novamente mais tarde"));
    }

    private ApiResponse errorResponse(String description){
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(
                        org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                        new MediaType().schema(new Schema<>().$ref(ERROR_SCHEMA_REF))
                ));
    }
}
