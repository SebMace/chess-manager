package clubmanagement.domain.club.vo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SeasonTests {

    @Test
    void should_distinguish_different_seasons() {
        Season firstSeason = new Season(2026, 2027);
        Season nextSeason = new Season(2027, 2028);

        assertNotEquals(firstSeason, nextSeason);
    }
}