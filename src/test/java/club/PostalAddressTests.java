package club;

import domain.club.vo.PostalAddress;
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
}
