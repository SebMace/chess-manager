package clubmanagement.ports;

import clubmanagement.domain.club.ClubRelationship;
import clubmanagement.domain.club.RelationshipStatus;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.person.Person;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** What every ClubRelationshipRepository promises; each implementation runs these tests against itself. */
public abstract class ClubRelationshipRepositoryContract {

    protected abstract ClubRelationshipRepository relationships();

    /** A club the implementation knows, created afresh for each test. */
    protected abstract ClubId aClub();

    /** A licensed player the implementation knows, created afresh for each test. */
    protected abstract Person aLicensedPlayer();

    @Test
    void should_find_the_membership_of_a_licensed_player_with_the_license_of_the_season() {
        ClubRelationshipRepository relationships = relationships();
        Person camille = aLicensedPlayer();
        ClubId club = aClub();
        Season season = new Season(2026, 2027);
        relationships.save(ClubRelationship.membershipOf(camille, club, FfeLicenseType.A, season));

        ClubRelationship found = relationships.find(camille.id(), club).orElseThrow();

        assertEquals(RelationshipStatus.MEMBER, found.status());
        assertEquals(Optional.of(FfeLicenseType.A), found.license(season));
    }
}
