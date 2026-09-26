package domain.commune;

/** The INSEE code of a commune (code officiel géographique), e.g. 45234 for Orléans. */
public record CommuneCode(String value) {
    public CommuneCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("A commune code cannot be null or blank");
        }
        value = value.strip();
    }
}
