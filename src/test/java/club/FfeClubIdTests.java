package club;

import domain.club.vo.FfeClubId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FfeClubIdTests {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void should_reject_an_ffe_club_identifier_without_a_value(String value) {
        assertThrows(IllegalArgumentException.class, () -> new FfeClubId(value));
    }

    @Test
    void should_write_the_ffe_club_identifier_in_upper_case_without_surrounding_spaces() {
        assertEquals(new FfeClubId("G45001"), new FfeClubId("  g45001 "));
    }
}
