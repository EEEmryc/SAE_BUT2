package sae.learnhub.learnhub.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                Server currentServer = new Server();
                currentServer.setUrl("/");
                currentServer.setDescription("Serveur courant (local ou Dev Tunnel)");

                return new OpenAPI()
                                .info(new Info()
                                                .title("LearnHub API")
                                                .version("1.0.0"))
                                .servers(List.of(currentServer))
                                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                                .components(new io.swagger.v3.oas.models.Components()
                                                .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
        }

        private SecurityScheme createAPIKeyScheme() {
                return new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat("JWT")
                                .scheme("bearer");
        }
}
