package br.com.CurriculoAi.config;

/**
 * Referências para as respostas de erro registradas em {@link OpenApiConfiguration}.
 * <p>
 * Uso nos controllers: {@code @ApiResponse(responseCode = "404", ref = NOT_FOUND)}.
 * Assim a descrição e o schema de erro ficam definidos em um único lugar.
 */
public final class OpenApiResponseRefs {

    private static final String PREFIX = "#/components/responses/";

    public static final String BAD_REQUEST_NAME = "BadRequest";
    public static final String UNAUTHORIZED_NAME = "Unauthorized";
    public static final String FORBIDDEN_NAME = "Forbidden";
    public static final String NOT_FOUND_NAME = "NotFound";
    public static final String BAD_GATEWAY_NAME = "BadGateway";
    public static final String SERVICE_UNAVAILABLE_NAME = "ServiceUnavailable";

    public static final String BAD_REQUEST = PREFIX + BAD_REQUEST_NAME;
    public static final String UNAUTHORIZED = PREFIX + UNAUTHORIZED_NAME;
    public static final String FORBIDDEN = PREFIX + FORBIDDEN_NAME;
    public static final String NOT_FOUND = PREFIX + NOT_FOUND_NAME;
    public static final String BAD_GATEWAY = PREFIX + BAD_GATEWAY_NAME;
    public static final String SERVICE_UNAVAILABLE = PREFIX + SERVICE_UNAVAILABLE_NAME;

    private OpenApiResponseRefs() {
    }
}
