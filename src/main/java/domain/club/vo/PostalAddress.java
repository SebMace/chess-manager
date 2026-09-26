package domain.club.vo;

/** A French postal address (NF Z10-011): street line, postcode and delivery town. */
public record PostalAddress(String street, String postcode, String town) {
    public PostalAddress {
        required(street, "street");
        required(postcode, "postcode");
        required(town, "town");
        postcode = postcode.replace(" ", "");
        if (!postcode.matches("\\d{5}")) throw new IllegalArgumentException("A postcode is made of five digits");
    }

    private static void required(String part, String name) {
        if (part == null || part.isBlank()) throw new IllegalArgumentException("An address cannot be without its " + name);
    }
}
