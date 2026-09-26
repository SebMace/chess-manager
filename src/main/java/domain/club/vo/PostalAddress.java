package domain.club.vo;

/** A French postal address (NF Z10-011): street line, postcode and delivery town. */
public record PostalAddress(String street, String postcode, String town) {
}
