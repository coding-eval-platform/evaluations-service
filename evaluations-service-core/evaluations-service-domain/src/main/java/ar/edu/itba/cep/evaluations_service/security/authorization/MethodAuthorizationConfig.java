package ar.edu.itba.cep.evaluations_service.security.authorization;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * Configuration class for method authorization stuff.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodAuthorizationConfig extends GlobalMethodSecurityConfiguration {
}
