package com.example.person.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Személy Nyilvántartó Rendszer API")
                        .version("1.0.0")
                        .description("")
                        .contact(new Contact()
                                .name("Nádasy Bendegúz")
                                .email("nadasy.bendeguz@gmail.com")
                                .url("https://github.com/BendeguzNadasy")
                        )
                );
    }
}