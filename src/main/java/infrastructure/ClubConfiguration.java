package infrastructure;

import clubmanagement.createclub.rest.CreateClubController;
import clubmanagement.defineopeninghours.DefineOpeningHours;
import clubmanagement.defineopeninghours.rest.DefineOpeningHoursController;
import clubmanagement.communesofcommittee.rest.CommunesOfCommitteeController;
import clubmanagement.insee.InseeCommunes;
import clubmanagement.openinghoursofclub.OpeningHoursOfClub;
import clubmanagement.openinghoursofclub.rest.OpeningHoursOfClubController;
import clubmanagement.persistence.JdbcClubCalendar;
import clubmanagement.laposte.LaPosteDeliveryTowns;
import clubmanagement.persistence.JdbcClubRepository;
import clubmanagement.ports.ClubCalendar;
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
import org.springframework.transaction.support.TransactionOperations;

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
    ClubCalendar clubCalendar(JdbcClient jdbc, TransactionOperations transactions) {
        return new JdbcClubCalendar(jdbc, transactions);
    }

    @Bean
    DefineOpeningHours defineOpeningHours(ClubCalendar clubCalendar) {
        return new DefineOpeningHours(clubCalendar);
    }

    @Bean
    DefineOpeningHoursController defineOpeningHoursController(DefineOpeningHours defineOpeningHours) {
        return new DefineOpeningHoursController(defineOpeningHours);
    }

    @Bean
    OpeningHoursOfClub openingHoursOfClub(ClubCalendar clubCalendar) {
        return new OpeningHoursOfClub(clubCalendar);
    }

    @Bean
    OpeningHoursOfClubController openingHoursOfClubController(OpeningHoursOfClub openingHoursOfClub) {
        return new OpeningHoursOfClubController(openingHoursOfClub);
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
