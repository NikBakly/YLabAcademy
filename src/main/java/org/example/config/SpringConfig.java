package org.example.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Wallet service Api",
                description = "Wallet service", version = "1.0.0",
                contact = @Contact(
                        name = "Baklykov Nikita"
                )
        )
)
public class SpringConfig {
}
