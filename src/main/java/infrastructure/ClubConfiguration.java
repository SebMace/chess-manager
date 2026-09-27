package infrastructure;

import clubmanagement.createclub.rest.CreateClubController;
import clubmanagement.communesofcommittee.rest.CommunesOfCommitteeController;
import clubmanagement.insee.InseeCommunes;
import clubmanagement.laposte.LaPosteDeliveryTowns;
import clubmanagement.persistence.JdbcClubRepository;
import clubmanagement.ports.ClubRepository;
import clubmanagement.clubsofcommittee.ClubsOfCommittee;
import clubmanagement.clubsofcommittee.rest.ClubsOfCommitteeController;
import clubmanagement.communesofcommittee.CommunesOfCommittee;
import clubmanagement.createclub.CreateClub;
import clubmanagement.ports.Communes;
import clubmanagement.ports.DeliveryTowns;
import clubmanagement.postcodesoftown.PostcodesOfTown;
import clubmanagement.postcodesoftown.rest.PostcodesOfTownController;
import clubmanagement.townsofcommune.TownsOfCommune;
import clubmanagement.townsofcommune.rest.TownsOfCommuneController;
import clubmanagement.townsofpostcode.TownsOfPostcode;
import clubmanagement.townsofpostcode.rest.TownsOfPostcodeController;
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

    @Bean
    ClubsOfCommittee clubsOfCommittee(ClubRepository clubs, Communes communes) {
        return new ClubsOfCommittee(clubs, communes);
    }

    @Bean
    ClubsOfCommitteeController clubsOfCommitteeController(ClubsOfCommittee clubsOfCommittee) {
        return new ClubsOfCommitteeController(clubsOfCommittee);
    }

    @Bean
    DeliveryTowns deliveryTowns() {
        return LaPosteDeliveryTowns.fromOfficialPostcodes();
    }

    @Bean
    TownsOfPostcode townsOfPostcode(DeliveryTowns deliveryTowns) {
        return new TownsOfPostcode(deliveryTowns);
    }

    @Bean
    TownsOfPostcodeController townsOfPostcodeController(TownsOfPostcode townsOfPostcode) {
        return new TownsOfPostcodeController(townsOfPostcode);
    }

    @Bean
    PostcodesOfTown postcodesOfTown(DeliveryTowns deliveryTowns) {
        return new PostcodesOfTown(deliveryTowns);
    }

    @Bean
    PostcodesOfTownController postcodesOfTownController(PostcodesOfTown postcodesOfTown) {
        return new PostcodesOfTownController(postcodesOfTown);
    }

    @Bean
    TownsOfCommune townsOfCommune(DeliveryTowns deliveryTowns) {
        return new TownsOfCommune(deliveryTowns);
    }

    @Bean
    TownsOfCommuneController townsOfCommuneController(TownsOfCommune townsOfCommune) {
        return new TownsOfCommuneController(townsOfCommune);
    }
}
