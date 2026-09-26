package clubmanagement.clubsofcommittee;

import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.commune.Commune;
import clubmanagement.ports.ClubRepository;
import clubmanagement.ports.Communes;

import java.util.List;

/** The clubs managed by the application in a departmental committee. */
public class ClubsOfCommittee {
    private final ClubRepository clubs;
    private final Communes communes;

    public ClubsOfCommittee(ClubRepository clubs, Communes communes) {
        this.clubs = clubs;
        this.communes = communes;
    }

    public List<ClubOfCommittee> execute(CommitteeCode committee) {
        return clubs.inCommittee(committee).stream().map(this::shown).toList();
    }

    // A club of a committee was created with its FFE identifier, in a commune of the reference.
    private ClubOfCommittee shown(Club club) {
        return new ClubOfCommittee(club.name(),
                communes.find(club.commune()).map(Commune::name).orElseThrow(),
                club.ffeClubId().orElseThrow());
    }
}
