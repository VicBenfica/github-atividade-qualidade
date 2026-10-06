package com.exemplo.tarefas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tarefasOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tarefas API")
                        .description("API simples de exemplo para ensinar testes unitarios com Spring Boot")
                        .version("v1"));
    }
}
