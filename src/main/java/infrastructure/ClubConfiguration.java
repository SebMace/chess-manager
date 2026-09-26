package infrastructure;

import adapters.in.rest.ClubController;
import adapters.in.rest.CommuneController;
import adapters.out.insee.InseeCommunes;
import adapters.out.persistence.JdbcClubRepository;
import clubmanagement.ports.ClubRepository;
import application.club.CommunesOfCommittee;
import application.club.CreateClub;
import clubmanagement.ports.Communes;
import clubmanagement.domain.club.vo.ClubId;
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

    @Bean
    CommunesOfCommittee communesOfCommittee(Communes communes) {
        return new CommunesOfCommittee(communes);
    }

    @Bean
    CommuneController communeController(CommunesOfCommittee communesOfCommittee) {
        return new CommuneController(communesOfCommittee);
    }
}
