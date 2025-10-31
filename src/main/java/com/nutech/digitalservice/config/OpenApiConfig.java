package com.nutech.digitalservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${app.base.url:http://localhost:8081}")
    private String baseUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server()
                .url(baseUrl)
                .description(getServerDescription());

        return new OpenAPI()
                .addServersItem(server)
                .info(new Info()
                        .title("Digital Service API")
                        .description("API untuk Digital Service dengan modul Membership, Information, dan Transaction")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("app ")
                                .email("dihardmg@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("All APIs")
                .packagesToScan("com.nutech.digitalservice.controller")
                .build();
    }

    private String getServerDescription() {
        if (baseUrl.contains("localhost") || baseUrl.contains("127.0.0.1")) {
            return "Development server";
        } else if (baseUrl.contains("railway.app")) {
            return "Railway production server";
        } else {
            return "Production server";
        }
    }
}