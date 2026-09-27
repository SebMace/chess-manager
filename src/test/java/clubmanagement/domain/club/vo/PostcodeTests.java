package clubmanagement.domain.club.vo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostcodeTests {
    @Test
    void should_reject_a_postcode_with_fewer_than_five_digits() {
        assertThrows(IllegalArgumentException.class, () -> new Postcode("4500"));
    }

    @Test
    void should_reject_a_postcode_that_is_not_made_of_digits() {
        assertThrows(IllegalArgumentException.class, () -> new Postcode("45A00"));
    }

    @Test
    void should_ignore_the_spaces_typed_in_a_postcode() {
        assertEquals(new Postcode("45000"), new Postcode(" 45 000 "));
    }

    @Test
    void should_reject_a_missing_postcode() {
        assertThrows(IllegalArgumentException.class, () -> new Postcode(null));
    }
}
