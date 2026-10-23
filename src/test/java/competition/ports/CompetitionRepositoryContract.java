package competition.ports;

import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.League;
import competition.domain.Season;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** What every CompetitionRepository promises; each implementation runs these tests against itself. */
public abstract class CompetitionRepositoryContract {

    protected abstract CompetitionRepository competitions();

    @Test
    void should_not_find_a_competition_that_was_never_saved() {
        CompetitionRepository competitions = competitions();

        assertEquals(Optional.empty(), competitions.find(new CompetitionId(new UUID(0, 1))));
    }

    @Test
    void should_find_a_competition_that_was_saved_with_its_state() {
        CompetitionRepository competitions = competitions();
        CompetitionId id = new CompetitionId(new UUID(0, 2));
        competitions.save(new Competition(id, "Nationale 4 Île-de-France", new Season(2026, 2027), 8)
                .organizedBy(new League("Île-de-France")));

        Competition found = competitions.find(id).orElseThrow();

        assertEquals(id, found.id());
        assertEquals("Nationale 4 Île-de-France", found.name());
        assertEquals(new Season(2026, 2027), found.season());
        assertEquals(8, found.boards());
    }

    @Test
    void should_find_the_league_that_organizes_a_saved_competition() {
        CompetitionRepository competitions = competitions();
        CompetitionId id = new CompetitionId(new UUID(0, 3));
        competitions.save(new Competition(id, "Nationale 4 Île-de-France", new Season(2026, 2027), 8)
                .organizedBy(new League("Île-de-France")));

        Competition found = competitions.find(id).orElseThrow();

        assertEquals(new League("Île-de-France"), found.organizingLeague());
    }
}
