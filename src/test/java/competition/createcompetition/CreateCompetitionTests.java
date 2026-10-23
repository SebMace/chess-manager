package competition.createcompetition;

import competition.domain.CompetitionId;
import competition.domain.Season;
import competition.ports.InMemoryCompetitionRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateCompetitionTests {
    private static final Season SEASON = new Season(2026, 2027);
    private final CompetitionId newId = new CompetitionId(new UUID(0, 1));
    private final InMemoryCompetitionRepository competitions = new InMemoryCompetitionRepository();
    private final CreateCompetition createCompetition = new CreateCompetition(competitions, SEASON, () -> newId);

    @Test
    void should_save_the_new_competition_under_the_new_identifier() {
        CompetitionId created = createCompetition.createCompetition("Nationale 4 Centre-Val de Loire", 6);

        assertEquals(newId, created);
        assertTrue(competitions.find(created).isPresent());
    }

    @Test
    void should_save_the_name_of_the_competition() {
        CompetitionId created = createCompetition.createCompetition("Nationale 4 Centre-Val de Loire", 6);

        assertEquals("Nationale 4 Centre-Val de Loire", competitions.find(created).orElseThrow().name());
    }

    @Test
    void should_create_the_competition_for_the_current_season() {
        CompetitionId created = createCompetition.createCompetition("Nationale 4 Centre-Val de Loire", 6);

        assertEquals(SEASON, competitions.find(created).orElseThrow().season());
    }

    @Test
    void should_save_the_number_of_boards_of_the_competition() {
        CompetitionId created = createCompetition.createCompetition("Nationale 4 Centre-Val de Loire", 8);

        assertEquals(8, competitions.find(created).orElseThrow().boards());
    }
}
