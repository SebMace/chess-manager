package clubmanagement.domain.club.vo;

/** The postcode La Poste delivers a postal address with. */
public record Postcode(String value) {
    public Postcode {
        if (value == null) throw new IllegalArgumentException("A postcode cannot be missing");
        value = value.replace(" ", "");
        if (!value.matches("\\d{5}")) throw new IllegalArgumentException("A postcode is made of five digits");
    }
}
