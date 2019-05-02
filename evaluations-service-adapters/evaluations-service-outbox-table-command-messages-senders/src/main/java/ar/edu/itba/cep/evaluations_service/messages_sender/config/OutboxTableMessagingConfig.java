package ar.edu.itba.cep.evaluations_service.messages_sender.config;

import com.bellotapps.outbox_debezium.producer.JdbcMessageProducer;
import com.bellotapps.outbox_debezium.producer.MessageProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;

/**
 * Created by Juan Marcos Bellini on 2019-04-16.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.evaluations_service.messages_sender"
})
public class OutboxTableMessagingConfig {

    @Bean
    public MessageProducer messageProducer(final DataSource dataSource) {
        return new JdbcMessageProducer(
                "transactional_messaging",
                "outbox",
                () -> DataSourceUtils.getConnection(dataSource)
        );
    }
}
