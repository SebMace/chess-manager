package application;

import application.club.IsExternalPlayer;
import application.club.RegisterLicense;
import club.InMemoryClubRepository;
import domain.club.Club;
import domain.club.vo.ClubId;
import domain.club.vo.Season;
import domain.member.vo.FfeId;
import domain.member.vo.FfeLicense;
import domain.member.vo.FfeLicenseType;
import domain.person.vo.PersonId;
import org.junit.jupiter.api.Test;
import relationship.InMemoryClubRelationshipRepository;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class ExternalPlayerTests {
    private final InMemoryClubRepository clubs = new InMemoryClubRepository();
    private final InMemoryClubRelationshipRepository relationships = new InMemoryClubRelationshipRepository();
    private final ClubId orleans = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    private final ClubId gien = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
    private final PersonId person = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    private final Season season = new Season(2026, 2027);
    private final FfeLicense license = new FfeLicense(new FfeId("A12345"), FfeLicenseType.A);

    @Test
    void should_recognize_external_affiliation_without_managing_the_club() {
        clubs.save(new Club(gien, "Gien", false));
        licensedAt(gien, season);

        assertTrue(isExternalPlayer(season));
        assertFalse(clubs.find(gien).orElseThrow().managedByApplication());
        assertEquals(license, relationships.find(person, gien).orElseThrow().license(season).orElseThrow());
    }

    @Test
    void should_not_consider_a_player_of_a_managed_club_as_external() {
        clubs.save(new Club(orleans, "Orléans", true));
        licensedAt(orleans, season);

        assertFalse(isExternalPlayer(season));
    }

    @Test
    void should_not_consider_a_player_external_for_a_season_without_affiliation() {
        clubs.save(new Club(gien, "Gien", false));
        licensedAt(gien, new Season(2025, 2026));

        assertFalse(isExternalPlayer(season));
    }

    @Test
    void should_consider_a_player_affiliated_with_a_club_unknown_to_the_application_as_external() {
        licensedAt(gien, season);

        assertTrue(isExternalPlayer(season));
        assertTrue(clubs.find(gien).isEmpty());
    }

    private void licensedAt(ClubId clubId, Season licenseSeason) {
        new RegisterLicense(relationships, licenseSeason).execute(person, clubId, license);
    }

    private boolean isExternalPlayer(Season requestedSeason) {
        return new IsExternalPlayer(relationships, clubs).execute(person, requestedSeason);
    }
}
