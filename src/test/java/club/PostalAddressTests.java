package club;

import clubmanagement.domain.club.vo.PostalAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostalAddressTests {
    @Test
    void should_reject_an_address_without_its_town() {
        assertThrows(IllegalArgumentException.class, () -> new PostalAddress("12 rue des Échecs", "45000", " "));
    }

    @Test
    void should_reject_an_address_without_its_postcode() {
        assertThrows(IllegalArgumentException.class, () -> new PostalAddress("12 rue des Échecs", null, "Orléans"));
    }

    @Test
    void should_reject_an_address_without_its_street() {
        assertThrows(IllegalArgumentException.class, () -> new PostalAddress("", "45000", "Orléans"));
    }

    @Test
    void should_reject_a_postcode_with_fewer_than_five_digits() {
        assertThrows(IllegalArgumentException.class, () -> new PostalAddress("12 rue des Échecs", "4500", "Orléans"));
    }

    @Test
    void should_reject_a_postcode_with_more_than_five_digits() {
        assertThrows(IllegalArgumentException.class, () -> new PostalAddress("12 rue des Échecs", "450000", "Orléans"));
    }

    @Test
    void should_reject_a_postcode_that_is_not_made_of_digits() {
        assertThrows(IllegalArgumentException.class, () -> new PostalAddress("12 rue des Échecs", "45A00", "Orléans"));
    }

    @Test
    void should_ignore_the_spaces_typed_in_a_postcode() {
        assertEquals("45000", new PostalAddress("12 rue des Échecs", " 45 000 ", "Orléans").postcode());
    }
}
