package clubmanagement.ports;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;

import java.util.List;

/** Read-only: the members of a club join the players and their relationship with it, which no aggregate holds. */
public interface ClubMembers {
    List<ClubMember> ofClub(ClubId club, Season season);
}
