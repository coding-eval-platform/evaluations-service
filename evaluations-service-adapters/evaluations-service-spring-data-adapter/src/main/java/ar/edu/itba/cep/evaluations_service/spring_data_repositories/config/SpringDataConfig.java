package ar.edu.itba.cep.evaluations_service.spring_data_repositories.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Spring Data Jpa Repositories.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.evaluations_service.spring_data_repositories"
})
// TODO: add @EnableJpaRepositories
// TODO: add @EntityScan
public class SpringDataConfig {
}
