package affiliation;

import domain.club.vo.ClubId;
import domain.club.vo.Season;
import domain.member.entities.Member;
import domain.member.vo.MemberId;
import domain.member.vo.FfeId;
import domain.member.vo.FfeLicense;
import domain.member.vo.FfeLicenseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AffiliationsTests {
    private Member member;
    private ClubId firstClub;
    private ClubId anotherClub;
    private Season firstSeason;
    private Season nextSeason;

    @BeforeEach
    void setUp() {
        member = new Member(
                new MemberId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                "Anatoly", "Karpov",
                new FfeLicense(new FfeId("A12345"), FfeLicenseType.A)
        );
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
        member.affiliateTo(firstClub, firstSeason);

        // Then: the affiliation identifies the club for that season
        assertEquals(Optional.of(firstClub), member.club(firstSeason));
    }

    @Test
    void should_preserve_previous_affiliation_when_joining_another_club_next_season() {
        // Given
        member.affiliateTo(firstClub, firstSeason);

        // When
        member.affiliateTo(anotherClub, nextSeason);

        // Then
        assertEquals(Optional.of(firstClub), member.club(firstSeason));
        assertEquals(Optional.of(anotherClub), member.club(nextSeason));
    }

    @Test
    void should_have_no_club_for_a_season_without_affiliation() {
        // Given
        member.affiliateTo(firstClub, firstSeason);

        // When
        Optional<ClubId> clubIdOptional = member.club(nextSeason);

        // Then
        assertEquals(Optional.empty(), clubIdOptional);
    }

    @Test
    void should_leave_affiliation_unchanged_when_affiliating_to_the_same_club_for_the_same_season() {
        // Given: a member already affiliated to a club for the season
        member.affiliateTo(firstClub, firstSeason);

        // When: the same affiliation is requested again
        member.affiliateTo(firstClub, firstSeason);

        // Then: the affiliation remains unchanged
        assertEquals(Optional.of(firstClub), member.club(firstSeason));
    }

    @Test
    void should_reject_affiliation_without_a_club() {
        // Given: a member without an affiliation for the season
        // When / Then: affiliation without a club is rejected
        assertThrows(IllegalArgumentException.class,
                () -> member.affiliateTo(null, firstSeason));

        assertEquals(Optional.empty(), member.club(firstSeason));
    }

    @Test
    void should_reject_affiliation_without_a_season() {
        // Given: a member and a club
        // When / Then: affiliation without a season is rejected
        assertThrows(IllegalArgumentException.class,
                () -> member.affiliateTo(firstClub, null));
    }

    @Test
    void should_reject_affiliation_to_another_club_for_the_same_season() {
        // Given: a member already affiliated to a club for the season
        member.affiliateTo(firstClub, firstSeason);

        // When / Then: another club is rejected and the original affiliation remains
        assertThrows(IllegalStateException.class, () -> member.affiliateTo(anotherClub, firstSeason));
        assertEquals(Optional.of(firstClub), member.club(firstSeason));
    }
}
