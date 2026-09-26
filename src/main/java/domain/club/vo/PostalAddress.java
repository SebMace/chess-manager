package domain.club.vo;

/** A French postal address (NF Z10-011): street line, postcode and delivery town. */
public record PostalAddress(String street, String postcode, String town) {
    public PostalAddress {
        if (street == null || street.isBlank()) throw new IllegalArgumentException("An address cannot be without its street");
        if (postcode == null || postcode.isBlank()) throw new IllegalArgumentException("An address cannot be without its postcode");
        if (town == null || town.isBlank()) throw new IllegalArgumentException("An address cannot be without its town");
    }
}
