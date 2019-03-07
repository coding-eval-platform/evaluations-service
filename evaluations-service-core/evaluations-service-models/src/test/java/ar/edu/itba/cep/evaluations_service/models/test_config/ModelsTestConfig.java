package ar.edu.itba.cep.evaluations_service.models.test_config;

import ar.edu.itba.cep.evaluations_service.models.config.ModelsConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration class for the model's module tests.
 */
@Configuration
@Import(ModelsConfig.class)
public class ModelsTestConfig {
}
