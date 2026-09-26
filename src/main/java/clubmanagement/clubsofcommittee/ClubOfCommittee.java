package clubmanagement.clubsofcommittee;

import clubmanagement.domain.club.vo.FfeClubId;

/** What an administrator is shown of a club of a departmental committee. */
public record ClubOfCommittee(String name, String commune, FfeClubId ffeClubId) {
}
