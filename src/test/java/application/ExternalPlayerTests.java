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
    @Test
    void should_recognize_external_affiliation_without_managing_the_club() {
        var clubs = new InMemoryClubRepository();
        var relationships = new InMemoryClubRelationshipRepository();
        var gien = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
        var person = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        var season = new Season(2026, 2027);
        clubs.save(new Club(gien, "Gien", false));
        var license = new FfeLicense(new FfeId("A12345"), FfeLicenseType.A);
        new RegisterLicense(relationships, season).execute(person, gien, license);

        assertTrue(new IsExternalPlayer(relationships, clubs).execute(person, season));
        assertFalse(clubs.find(gien).orElseThrow().managedByApplication());
        assertEquals(license, relationships.find(person, gien).orElseThrow().license(season).orElseThrow());
    }
}
