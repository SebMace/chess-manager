package affiliation;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.registerlicense.RegisterLicense;
import clubmanagement.domain.club.ClubAffiliations;
import relationship.InMemoryClubRelationshipRepository;
import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicense;
import clubmanagement.domain.member.vo.FfeLicenseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

 class AffiliationsTests {
    private PersonId personId;
    private final InMemoryClubRelationshipRepository relationships = new InMemoryClubRelationshipRepository();
    private final FfeLicense license = new FfeLicense(new FfeId("A12345"), FfeLicenseType.A);
    private ClubId firstClub;
    private ClubId anotherClub;
    private Season firstSeason;
    private Season nextSeason;

    @BeforeEach
    void setUp() {
        personId = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        firstClub = new ClubId(
                UUID.fromString("00000000-0000-0000-0000-000000000002")
        );
        anotherClub = new ClubId(
                UUID.fromString("00000000-0000-0000-0000-000000000003")
        );
        firstSeason = new Season(2026, 2027);
        nextSeason = new Season(2027, 2028);
    }

    @Test
    void should_affiliate_a_member_to_a_club_for_a_season() {
        // Given: a member without an affiliation for the season
        // When: the member joins the club for that season
        affiliateTo(firstClub, firstSeason);

        // Then: the affiliation identifies the club for that season
        assertEquals(Optional.of(firstClub), club(firstSeason));
    }

    @Test
    void should_preserve_previous_affiliation_when_joining_another_club_next_season() {
        // Given
        affiliateTo(firstClub, firstSeason);

        // When
        affiliateTo(anotherClub, nextSeason);

        // Then
        assertEquals(Optional.of(firstClub), club(firstSeason));
        assertEquals(Optional.of(anotherClub), club(nextSeason));
    }

    @Test
    void should_have_no_club_for_a_season_without_affiliation() {
        // Given
        affiliateTo(firstClub, firstSeason);

        // When
        Optional<ClubId> clubIdOptional = club(nextSeason);

        // Then
        assertEquals(Optional.empty(), clubIdOptional);
    }

    @Test
    void should_leave_affiliation_unchanged_when_affiliating_to_the_same_club_for_the_same_season() {
        // Given: a member already affiliated to a club for the season
        affiliateTo(firstClub, firstSeason);

        // When: the same affiliation is requested again
        affiliateTo(firstClub, firstSeason);

        // Then: the affiliation remains unchanged
        assertEquals(Optional.of(firstClub), club(firstSeason));
    }

    @Test
    void should_reject_affiliation_without_a_club() {
        // Given: a member without an affiliation for the season
        // When / Then: affiliation without a club is rejected
        assertThrows(IllegalArgumentException.class,
                () -> affiliateTo(null, firstSeason));

        assertEquals(Optional.empty(), club(firstSeason));
    }

    @Test
    void should_reject_affiliation_without_a_season() {
        // Given: a member and a club
        // When / Then: affiliation without a season is rejected
        assertThrows(IllegalArgumentException.class,
                () -> affiliateTo(firstClub, null));
    }

    @Test
    void should_reject_affiliation_to_another_club_for_the_same_season() {
        // Given: a member already affiliated to a club for the season
        affiliateTo(firstClub, firstSeason);

        // When / Then: another club is rejected and the original affiliation remains
        assertThrows(IllegalStateException.class, () -> affiliateTo(anotherClub, firstSeason));
        assertEquals(Optional.of(firstClub), club(firstSeason));
    }
    private void affiliateTo(ClubId clubId, Season currentSeason) {
        new RegisterLicense(relationships, currentSeason).execute(personId, clubId, license);
    }

    private Optional<ClubId> club(Season season) {
        return new ClubAffiliations(relationships.findByPerson(personId)).club(season);
    }
}
