package clubmanagement.registerlicense;

import static clubmanagement.domain.club.RelationshipStatus.*;
import clubmanagement.domain.club.ClubRelationship;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicense;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.person.Person;
import clubmanagement.domain.person.vo.PersonId;
import org.junit.jupiter.api.Test;
import clubmanagement.ports.InMemoryClubRelationshipRepository;
import clubmanagement.ports.InMemoryPersonRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterLicenseTests {
    private final PersonId personId = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    private final ClubId orleans = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    private final ClubId olivet = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
    private final Season season = new Season(2026, 2027);
    private final FfeLicense license = new FfeLicense(new FfeId("A12345"), FfeLicenseType.A);
    private final InMemoryClubRelationshipRepository repository = new InMemoryClubRelationshipRepository();
    private final InMemoryPersonRepository people = new InMemoryPersonRepository();

    @Test
    void a_prospect_taking_their_first_license_receives_their_ffe_identifier_and_becomes_a_member() {
        people.save(new Person(personId, "Camille", "Martin"));
        repository.save(new ClubRelationship(personId, orleans));

        new RegisterLicense(people, repository, season)
                .registerFirstLicenseOf(personId, orleans, new FfeId("K58213"), FfeLicenseType.A);

        assertEquals(Optional.of(new FfeId("K58213")), people.find(personId).orElseThrow().ffeId());
        ClubRelationship membership = repository.find(personId, orleans).orElseThrow();
        assertEquals(MEMBER, membership.status());
        assertEquals(FfeLicenseType.A, membership.license(season).orElseThrow().type());
    }
    @Test
    void a_licensed_player_renewing_their_license_keeps_their_ffe_identifier() {
        Person camille = Person.licensedPlayer(personId, "Camille", "Martin", new FfeId("K58213"));
        people.save(camille);
        repository.save(ClubRelationship.membershipOf(camille, orleans, FfeLicenseType.A, season));
        Season nextSeason = new Season(2027, 2028);

        new RegisterLicense(people, repository, nextSeason).renewLicenseOf(personId, orleans, FfeLicenseType.B);

        assertEquals(Optional.of(new FfeId("K58213")), people.find(personId).orElseThrow().ffeId());
        assertEquals(FfeLicenseType.B, repository.find(personId, orleans).orElseThrow().license(nextSeason).orElseThrow().type());
    }
    @Test
    void should_save_membership_for_the_current_season() {
        ClubRelationship prospect = new ClubRelationship(personId, orleans);
        repository.save(prospect);

        new RegisterLicense(people, repository, season).registerLicenseOf(personId, orleans, license);

        ClubRelationship saved = repository.find(personId, orleans).orElseThrow();
        assertEquals(MEMBER, saved.status());
        assertEquals(license, saved.license(season).orElseThrow());
        assertEquals(PROSPECT, prospect.status());
    }
    @Test
    void should_break_other_prospect_relationships_by_default() {
        repository.save(new ClubRelationship(personId, orleans));
        repository.save(new ClubRelationship(personId, olivet));
        PersonId anotherPerson = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000004"));
        repository.save(new ClubRelationship(anotherPerson, olivet));

        new RegisterLicense(people, repository, season).registerLicenseOf(personId, orleans, license);

        assertTrue(repository.find(personId, olivet).isEmpty());
        assertEquals(MEMBER, repository.find(personId, orleans).orElseThrow().status());
        assertEquals(PROSPECT, repository.find(anotherPerson, olivet).orElseThrow().status());
    }
    @Test
    void should_keep_only_explicitly_requested_partnerships() {
        ClubId gien = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000005"));
        repository.save(new ClubRelationship(personId, orleans));
        repository.save(new ClubRelationship(personId, olivet));
        repository.save(new ClubRelationship(personId, gien));

        new RegisterLicense(people, repository, season).registerLicenseOfKeepingPartnerships(personId, orleans, license, Set.of(olivet));

        ClubRelationship partner = repository.find(personId, olivet).orElseThrow();
        assertEquals(PARTNER, partner.status());
        assertTrue(partner.license(season).isEmpty());
        assertTrue(repository.find(personId, gien).isEmpty());
        assertEquals(MEMBER, repository.find(personId, orleans).orElseThrow().status());
    }
    @Test
    void should_reject_a_second_club_in_the_same_season_without_changing_relationships() {
        repository.save(new ClubRelationship(personId, orleans).registerLicense(license, season));
        repository.save(new ClubRelationship(personId, olivet));

        assertThrows(IllegalStateException.class,
                () -> new RegisterLicense(people, repository, season).registerLicenseOf(personId, olivet, license));

        assertEquals(license, repository.find(personId, orleans).orElseThrow().license(season).orElseThrow());
        assertEquals(PROSPECT, repository.find(personId, olivet).orElseThrow().status());
    }
    @Test
    void should_register_membership_without_a_prior_prospect_relationship() {
        new RegisterLicense(people, repository, season).registerLicenseOf(personId, orleans, license);

        assertEquals(license, repository.find(personId, orleans).orElseThrow().license(season).orElseThrow());
    }
}
