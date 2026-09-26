package domain.club.vo;

import domain.commune.DepartmentCode;

public record CommitteeCode(String value) {
    /** Metropolitan France only: overseas committees (e.g. 9C for Guadeloupe) will need their own mapping. */
    public DepartmentCode department() {
        return new DepartmentCode(value);
    }
}
