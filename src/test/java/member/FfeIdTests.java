package member;

import domain.member.vo.FfeId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FfeIdTests {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void should_reject_an_ffe_id_without_a_value(String value) {
        assertThrows(IllegalArgumentException.class, () -> new FfeId(value));
    }

    @Test
    void should_preserve_the_ffe_identifier_value() {
        assertEquals("A00123", new FfeId("A00123").value());
    }
}
