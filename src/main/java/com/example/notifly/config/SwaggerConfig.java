package com.example.notifly.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notifly API")
                        .version("1.0.0")
                        .description("API documentation for the Notifly application.")
                        .termsOfService("https://notifly.com/terms")
                        .contact(new Contact()
                                .name("Support Team")
                                .email("support@notifly.com")
                                .url("https://notifly.com"))
                );
    }
}
