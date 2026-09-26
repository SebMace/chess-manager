package application.club;

import clubmanagement.ports.Communes;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.commune.Commune;

import java.util.List;

/** The communes a club of a departmental committee can be located in. */
public class CommunesOfCommittee {
    private final Communes communes;

    public CommunesOfCommittee(Communes communes) {
        this.communes = communes;
    }

    public List<Commune> execute(CommitteeCode committee) {
        return communes.inDepartment(committee.department());
    }
}
