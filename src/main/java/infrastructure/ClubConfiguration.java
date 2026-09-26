package infrastructure;

import clubmanagement.createclub.rest.CreateClubController;
import clubmanagement.communesofcommittee.rest.CommunesOfCommitteeController;
import clubmanagement.insee.InseeCommunes;
import clubmanagement.persistence.JdbcClubRepository;
import clubmanagement.ports.ClubRepository;
import clubmanagement.communesofcommittee.CommunesOfCommittee;
import clubmanagement.createclub.CreateClub;
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
    CreateClubController createClubController(CreateClub createClub) {
        return new CreateClubController(createClub);
    }

    @Bean
    CommunesOfCommittee communesOfCommittee(Communes communes) {
        return new CommunesOfCommittee(communes);
    }

    @Bean
    CommunesOfCommitteeController communesOfCommitteeController(CommunesOfCommittee communesOfCommittee) {
        return new CommunesOfCommitteeController(communesOfCommittee);
    }
}
