package br.com.CurriculoAi.client.cerebras;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class CerebrasClientConfiguration {
    @Bean
    public feign.codec.Decoder utf8JsonDecoder() {
        return (response, type) -> {
            if (response.body() == null) return null;
            try (var input = response.body().asInputStream()) {
                return new String(input.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            }
        };
    }
    @Bean
    public RequestInterceptor authorization(@Value("${cerebras.api.key}") String apiKey) {
        return request -> request.header("Authorization", "Bearer " + apiKey);
    }
}
