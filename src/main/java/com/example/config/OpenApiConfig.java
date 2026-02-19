package com.example.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static org.springframework.ai.utility.AiConstants.*;

@Configuration
public class OpenApiConfig {

    @Value("${app.openapi.server-url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(apiInfo())
                .servers(List.of(new Server().url(serverUrl).description(DESCRIPTION2)))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, bearerSecurityScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title(SPRING_AI_API)
                .description(DESCRIPTION)
                .version(VERSION)
                .contact(new Contact().name(SPRING_AI_PROJECT).email(EMAIL_ID).url(GIT_HUB_URL))
                .license(new License().name(APACHE_2_0).url(LICENSE_URL));
    }

    private SecurityScheme bearerSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme(BEARER)
                .bearerFormat(JWT)
                .description(DESCRIPTION1);
    }
}