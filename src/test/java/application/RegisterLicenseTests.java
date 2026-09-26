package application;

import static clubmanagement.domain.club.RelationshipStatus.*;
import clubmanagement.registerlicense.RegisterLicense;
import clubmanagement.domain.club.ClubRelationship;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicense;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.person.vo.PersonId;
import org.junit.jupiter.api.Test;
import relationship.InMemoryClubRelationshipRepository;

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

    @Test
    void should_save_membership_for_the_current_season() {
        ClubRelationship prospect = new ClubRelationship(personId, orleans);
        repository.save(prospect);

        new RegisterLicense(repository, season).execute(personId, orleans, license);

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

        new RegisterLicense(repository, season).execute(personId, orleans, license);

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

        new RegisterLicense(repository, season).execute(personId, orleans, license, Set.of(olivet));

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
                () -> new RegisterLicense(repository, season).execute(personId, olivet, license));

        assertEquals(license, repository.find(personId, orleans).orElseThrow().license(season).orElseThrow());
        assertEquals(PROSPECT, repository.find(personId, olivet).orElseThrow().status());
    }
    @Test
    void should_register_membership_without_a_prior_prospect_relationship() {
        new RegisterLicense(repository, season).execute(personId, orleans, license);

        assertEquals(license, repository.find(personId, orleans).orElseThrow().license(season).orElseThrow());
    }
    @Test
    void should_validate_license_details_at_the_application_boundary() {
        RegisterLicense register = new RegisterLicense(repository, season);
        assertThrows(IllegalArgumentException.class,
                () -> register.execute(personId, orleans, " ", FfeLicenseType.A));
        assertTrue(repository.find(personId, orleans).isEmpty());
        register.execute(personId, orleans, "A00123", FfeLicenseType.B);
        assertEquals("A00123", repository.find(personId, orleans).orElseThrow().license(season).orElseThrow().ffeId().value());
    }
}
