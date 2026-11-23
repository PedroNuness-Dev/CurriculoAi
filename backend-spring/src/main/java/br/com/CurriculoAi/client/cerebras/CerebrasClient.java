package br.com.CurriculoAi.client.cerebras;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "cerebras", url = "${cerebras.api.url}", configuration = CerebrasClientConfiguration.class)
public interface CerebrasClient {
    @PostMapping(consumes = "application/json;charset=UTF-8", produces = "application/json")
    String complete(@RequestBody String request);
}
