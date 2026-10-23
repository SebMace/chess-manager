package clubmanagement.membersofclub;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.EloRating;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.ports.ClubMember;
import clubmanagement.ports.ClubMembers;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MembersOfClubTests {
    private static final ClubId CLUB = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    private static final Season CURRENT_SEASON = new Season(2026, 2027);

    @Test
    void should_show_the_members_of_the_club_for_the_current_season() {
        ClubMember camille = new ClubMember("Camille", "Martin", new FfeId("K58213"), FfeLicenseType.A,
                Optional.of(new EloRating(1850)), Optional.empty());
        ClubMembers members = (club, season) ->
                club.equals(CLUB) && season.equals(CURRENT_SEASON) ? List.of(camille) : List.of();

        assertEquals(List.of(camille), new MembersOfClub(members, CURRENT_SEASON).membersOf(CLUB));
    }
}
