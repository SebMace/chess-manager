package relationship;

import static domain.club.RelationshipStatus.*;
import domain.person.vo.PersonId;
import domain.club.vo.Season;
import domain.member.vo.FfeId;
import domain.member.vo.FfeLicense;
import domain.member.vo.FfeLicenseType;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import domain.club.vo.ClubId;
import domain.club.ClubRelationship;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClubRelationshipTests {

    @Test
    void should_reject_a_prospect_relationship_without_a_club_id() {
        // Given: a person, but no club
        PersonId personId = new PersonId(UUID.fromString(
                "00000000-0000-0000-0000-000000000001"));

        // When / Then: the relationship cannot be created
        assertThrows(IllegalArgumentException.class,
                () -> new ClubRelationship(personId, null));
    }

    @Test
    void should_reject_a_prospect_relationship_without_a_person_id() {
        // Given: a club, but no person identity
        ClubId clubId = new ClubId(UUID.fromString(
                "00000000-0000-0000-0000-000000000002"));

        // When / Then: the relationship cannot be created
        assertThrows(IllegalArgumentException.class,
                () -> new ClubRelationship(null, clubId));
    }

    @Test
    void should_identify_the_person_and_club_of_a_prospect_relationship() {
        // Given: a person and a club
        PersonId personId = new PersonId(UUID.fromString(
                "00000000-0000-0000-0000-000000000001"));
        ClubId clubId = new ClubId(UUID.fromString(
                "00000000-0000-0000-0000-000000000002"));

        // When: their prospect relationship is created
        ClubRelationship relationship =
                new ClubRelationship(personId, clubId);

        // Then: the relationship retains both identities
        assertEquals(personId, relationship.personId());
        assertEquals(clubId, relationship.clubId());
    }
    @Test
    void should_record_a_prospect_relationship() {
        PersonId personId = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        ClubId clubId = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));

        ClubRelationship relationship = new ClubRelationship(personId, clubId);

        assertEquals(PROSPECT, relationship.status());
    }
    @Test
    void should_become_a_member_for_the_license_season() {
        PersonId personId = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        ClubId clubId = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        ClubRelationship prospect = new ClubRelationship(personId, clubId);
        Season season = new Season(2026, 2027);
        FfeLicense license = new FfeLicense(new FfeId("A12345"), FfeLicenseType.A);

        ClubRelationship member = prospect.registerLicense(license, season);

        assertEquals(MEMBER, member.status());
        assertNotEquals(PROSPECT, member.status());
        assertEquals(license, member.license(season).orElseThrow());
        assertEquals(personId, member.personId());
        assertEquals(clubId, member.clubId());
        assertEquals(PROSPECT, prospect.status());
    }
    @Test
    void should_reject_membership_without_a_license() {
        ClubRelationship prospect = new ClubRelationship(
                new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002")));

        assertThrows(IllegalArgumentException.class,
                () -> prospect.registerLicense(null, new Season(2026, 2027)));
        assertEquals(PROSPECT, prospect.status());
    }

    @Test
    void should_reject_membership_without_a_season() {
        ClubRelationship prospect = new ClubRelationship(
                new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002")));

        assertThrows(IllegalArgumentException.class,
                () -> prospect.registerLicense(new FfeLicense(new FfeId("A12345"), FfeLicenseType.A), null));
        assertEquals(PROSPECT, prospect.status());
    }
    @Test
    void should_preserve_license_history_when_renewing_for_another_season() {

        // Given
        ClubRelationship prospect = new ClubRelationship(
                new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002")));
        Season firstSeason = new Season(2026, 2027);
        Season nextSeason = new Season(2027, 2028);
        FfeLicense firstLicense = new FfeLicense(new FfeId("A12345"), FfeLicenseType.A);
        FfeLicense nextLicense = new FfeLicense(new FfeId("A12345"), FfeLicenseType.B);

        // When
        ClubRelationship member = prospect.registerLicense(firstLicense, firstSeason);
        ClubRelationship renewed = member.registerLicense(nextLicense, nextSeason);

        //Then
        assertEquals(firstLicense, renewed.license(firstSeason).orElseThrow());
        assertEquals(nextLicense, renewed.license(nextSeason).orElseThrow());
        assertEquals(firstLicense, member.license(firstSeason).orElseThrow());
        assertEquals(java.util.Optional.empty(), member.license(nextSeason));
    }
}
