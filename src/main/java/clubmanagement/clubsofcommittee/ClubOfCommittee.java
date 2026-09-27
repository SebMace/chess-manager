package clubmanagement.clubsofcommittee;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.FfeClubId;

/** What an administrator is shown of a club of a departmental committee. */
public record ClubOfCommittee(ClubId id, String name, String commune, FfeClubId ffeClubId) {
}
