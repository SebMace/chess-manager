package domain.commune;

/** A commune of the official geographic code, as the club screens need it. */
public record Commune(CommuneCode code, String name, DepartmentCode department) {
}
