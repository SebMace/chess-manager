package infrastructure;

import application.club.CreateClub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
@Profile("dev")
class DevelopmentConfiguration {
    @Bean
    DevelopmentData developmentData(JdbcClient jdbc, CreateClub createClub) {
        return new DevelopmentData(jdbc, createClub);
    }
}
