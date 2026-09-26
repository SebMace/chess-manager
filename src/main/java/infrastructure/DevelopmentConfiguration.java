package infrastructure;

import application.club.CreateClub;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
class DevelopmentConfiguration {
    @Bean
    DevelopmentData developmentData(CreateClub createClub) {
        return new DevelopmentData(createClub);
    }
}
