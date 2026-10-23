package competition.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompetitionTests {
    private static final CompetitionId ID = new CompetitionId(new UUID(0, 1));
    private static final League LEAGUE = new League("Centre-Val de Loire");
    private static final Season SEASON = new Season(2026, 2027);

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void should_refuse_a_competition_without_board(int boards) {
        assertThrows(CompetitionNeedsAtLeastOneBoard.class,
                () -> new Competition(ID, "Nationale 4", LEAGUE, SEASON, boards));
    }

    @Test
    void should_accept_a_competition_with_a_single_board() {
        assertEquals(1, new Competition(ID, "Nationale 4", LEAGUE, SEASON, 1).boards());
    }
}
