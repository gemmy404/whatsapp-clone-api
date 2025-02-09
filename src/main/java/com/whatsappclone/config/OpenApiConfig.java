package com.whatsappclone.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Whatsapp Clone API",
                version = "1.0",
                description = "Real-time chat application built using modern technologies to enable seamless communication between users."
        ),
        servers = @Server(
                description = "Local ENV",
                url = "http://localhost:8080"
        ),
        security = {
                @SecurityRequirement(
                        name = "keycloak"
                )
        }
)
@SecurityScheme(
        name = "keycloak",
        description = "OAuth2 authentication with Keycloak",
        type = SecuritySchemeType.OAUTH2,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "http://localhost:9090/realms/whatsapp-clone/protocol/openid-connect/auth",
                        tokenUrl = "http://localhost:9090/realms/whatsapp-clone/protocol/openid-connect/token",
                        scopes = {
                                @OAuthScope(name = "openid", description = "OpenID Connect scope"),
                                @OAuthScope(name = "profile", description = "Access user profile info"),
                                @OAuthScope(name = "email", description = "Access user email")
                        }
                )
        )
)
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi setUpGroupedOpenApi() {
        String packages = "com.whatsappclone.controller";
        return GroupedOpenApi.builder()
                .group("Whatsapp Clone")
                .packagesToScan(packages)
                .build();
    }

}
