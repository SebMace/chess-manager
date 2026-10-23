package clubmanagement.membersofclub;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.ports.ClubMember;
import clubmanagement.ports.ClubMembers;

import java.util.List;

/** The players who are members of a club for the current season. */
public class MembersOfClub {
    private final ClubMembers members;
    private final Season currentSeason;

    public MembersOfClub(ClubMembers members, Season currentSeason) {
        this.members = members;
        this.currentSeason = currentSeason;
    }

    public List<ClubMember> membersOf(ClubId club) {
        return members.ofClub(club, currentSeason);
    }
}
