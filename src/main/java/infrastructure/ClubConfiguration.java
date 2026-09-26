package infrastructure;

import adapters.in.rest.ClubController;
import adapters.out.insee.InseeCommunes;
import adapters.out.persistence.JdbcClubRepository;
import application.club.ClubRepository;
import application.club.CreateClub;
import application.commune.Communes;
import domain.club.vo.ClubId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.UUID;

@Configuration
class ClubConfiguration {
    @Bean
    ClubRepository clubRepository(JdbcClient jdbc) {
        return new JdbcClubRepository(jdbc);
    }

    @Bean
    Communes communes() {
        return InseeCommunes.fromOfficialGeographicCode();
    }

    @Bean
    CreateClub createClub(ClubRepository clubs, Communes communes) {
        return new CreateClub(clubs, communes, () -> new ClubId(UUID.randomUUID()));
    }

    @Bean
    ClubController clubController(CreateClub createClub) {
        return new ClubController(createClub);
    }
}
