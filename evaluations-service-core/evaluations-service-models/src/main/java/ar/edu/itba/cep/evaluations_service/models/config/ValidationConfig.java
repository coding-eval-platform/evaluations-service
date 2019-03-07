package ar.edu.itba.cep.evaluations_service.models.config;

import com.bellotapps.webapps_commons.validation.config.EnableValidationAspects;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;

/**
 * Configuration class for validation aspects.
 */
@Configuration
@EnableValidationAspects
@EnableSpringConfigured
public class ValidationConfig {
}
