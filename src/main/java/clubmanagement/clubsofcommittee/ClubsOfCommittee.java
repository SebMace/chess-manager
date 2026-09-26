package clubmanagement.clubsofcommittee;

import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.ports.ClubRepository;

import java.util.List;

/** The clubs managed by the application in a departmental committee. */
public class ClubsOfCommittee {
    private final ClubRepository clubs;

    public ClubsOfCommittee(ClubRepository clubs) {
        this.clubs = clubs;
    }

    public List<Club> execute(CommitteeCode committee) {
        return clubs.inCommittee(committee);
    }
}
