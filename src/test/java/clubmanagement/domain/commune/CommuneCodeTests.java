package clubmanagement.domain.commune;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommuneCodeTests {
    @Test
    void should_reject_a_blank_commune_code() {
        assertThrows(IllegalArgumentException.class, () -> new CommuneCode("   "));
    }
}
