package domain.club.vo;

import domain.commune.DepartmentCode;

public record CommitteeCode(String value) {
    public DepartmentCode department() {
        return null;
    }
}
