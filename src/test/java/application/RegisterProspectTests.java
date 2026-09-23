package application;

import static domain.club.RelationshipStatus.*;
import application.prospect.RegisterProspect;
import application.club.RegisterLicense;
import domain.member.vo.FfeLicense;
import domain.member.vo.FfeLicenseType;
import domain.member.vo.FfeId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import domain.club.vo.ClubId;
import domain.club.vo.Season;
import domain.person.Person;
import domain.person.vo.PersonId;
import org.junit.jupiter.api.Test;
import relationship.InMemoryClubRelationshipRepository;
import person.InMemoryPersonRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RegisterProspectTests {
    private final PersonId personId = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    private final ClubId clubId = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    private final Season season = new Season(2026, 2027);
    private final InMemoryPersonRepository people = new InMemoryPersonRepository();
    private final InMemoryClubRelationshipRepository relationships = new InMemoryClubRelationshipRepository();

    @Test
    void should_save_the_person_and_the_prospect_relationship() {
        RegisterProspect register = new RegisterProspect(people, relationships, season, () -> personId);

        PersonId registeredId = register.execute(clubId, "Camille", "Martin", "camille@example.org");

        assertEquals(personId, registeredId);
        Person person = people.find(registeredId).orElseThrow();
        assertEquals("Camille", person.firstName());
        assertEquals("Martin", person.lastName());
        assertEquals("camille@example.org", person.email().orElseThrow());
        assertEquals(PROSPECT, relationships.find(registeredId, clubId).orElseThrow().status());
    }
    @Test
    void should_register_the_same_person_as_a_prospect_of_another_club() {
        RegisterProspect register = new RegisterProspect(people, relationships, season, () -> personId);
        register.execute(clubId, "Camille", "Martin", "camille@example.org");
        ClubId anotherClub = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000003"));

        register.execute(personId, anotherClub);

        assertEquals(PROSPECT, relationships.find(personId, clubId).orElseThrow().status());
        assertEquals(PROSPECT, relationships.find(personId, anotherClub).orElseThrow().status());
        assertEquals("Camille", people.find(personId).orElseThrow().firstName());
    }
    @ParameterizedTest
    @EnumSource(FfeLicenseType.class)
    void should_reject_a_licensed_person_as_a_prospect_in_any_club(FfeLicenseType type) {
        RegisterProspect register = new RegisterProspect(people, relationships, season, () -> personId);
        register.execute(clubId, "Camille", "Martin", "camille@example.org");
        FfeLicense license = new FfeLicense(new FfeId("A12345"), type);
        new RegisterLicense(relationships, season).execute(personId, clubId, license);
        ClubId anotherClub = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000003"));

        assertThrows(IllegalStateException.class, () -> register.execute(personId, clubId));
        assertThrows(IllegalStateException.class, () -> register.execute(personId, anotherClub));

        assertEquals(MEMBER, relationships.find(personId, clubId).orElseThrow().status());
        assertEquals(license, relationships.find(personId, clubId).orElseThrow().license(season).orElseThrow());
        assertTrue(relationships.find(personId, anotherClub).isEmpty());
    }
}
