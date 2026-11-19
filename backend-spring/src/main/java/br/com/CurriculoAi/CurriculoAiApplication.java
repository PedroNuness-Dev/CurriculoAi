package br.com.CurriculoAi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@ConfigurationPropertiesScan
@EnableFeignClients
@org.springframework.boot.context.properties.ConfigurationPropertiesScan
@SpringBootApplication
public class CurriculoAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CurriculoAiApplication.class, args);
	}

}
