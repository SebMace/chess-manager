package clubmanagement.domain.club;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.person.Person;
import clubmanagement.domain.person.vo.PersonId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Optional;
import java.util.UUID;

import static clubmanagement.domain.club.RelationshipStatus.MEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** A person licensed by the FFE has a personal FFE identifier and is a member of their club for the season. */
class FfeMembershipTests {
    private final PersonId camilleId = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    private final ClubId orleans = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
    private final Season season = new Season(2026, 2027);

    @ParameterizedTest
    @EnumSource(FfeLicenseType.class)
    void a_licensed_player_is_a_member_of_their_club_for_the_season_of_either_license_type(FfeLicenseType licenseType) {
        Person camille = Person.licensedPlayer(camilleId, "Camille", "Martin", new FfeId("A12345"));

        ClubRelationship membership = ClubRelationship.membershipOf(camille, orleans, licenseType, season);

        assertEquals(MEMBER, membership.status());
        assertEquals(Optional.of(licenseType), membership.license(season));
        assertEquals(Optional.of(new FfeId("A12345")), camille.ffeId());
    }

    @Test
    void only_a_licensed_player_can_be_a_member() {
        Person prospect = new Person(camilleId, "Camille", "Martin");

        assertThrows(IllegalArgumentException.class,
                () -> ClubRelationship.membershipOf(prospect, orleans, FfeLicenseType.A, season));
    }

    @Test
    void a_license_needs_its_type() {
        ClubRelationship membership = ClubRelationship.membershipOf(
                Person.licensedPlayer(camilleId, "Camille", "Martin", new FfeId("A12345")), orleans, FfeLicenseType.A, season);

        assertThrows(IllegalArgumentException.class, () -> membership.registerLicense(null, season));

        assertEquals(Optional.of(FfeLicenseType.A), membership.license(season));
    }
}
