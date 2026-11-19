package br.com.CurriculoAi.client.cerebras;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class CerebrasClientConfiguration {
    @Bean
    public RequestInterceptor authorization(@Value("${cerebras.api.key}") String apiKey) {
        return request -> request.header("Authorization", "Bearer " + apiKey);
    }
}
