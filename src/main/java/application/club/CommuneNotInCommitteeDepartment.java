package application.club;

import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.commune.CommuneCode;

public class CommuneNotInCommitteeDepartment extends RuntimeException {
    private final CommuneCode commune;

    public CommuneNotInCommitteeDepartment(CommuneCode commune, CommitteeCode committee) {
        super("The commune " + commune.value() + " is not in the department of the committee " + committee.value());
        this.commune = commune;
    }

    public CommuneCode commune() { return commune; }
}
