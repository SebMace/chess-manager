package competition.ports;

import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.League;
import competition.domain.Season;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        competitions.save(new Competition(id, "Nationale 4 Île-de-France", new League("Île-de-France"),
                new Season(2026, 2027), 8));

        Competition found = competitions.find(id).orElseThrow();

        assertEquals(id, found.id());
        assertEquals("Nationale 4 Île-de-France", found.name());
        assertEquals(new Season(2026, 2027), found.season());
        assertEquals(8, found.boards());
    }

    @Test
    void should_find_only_the_saved_competitions_of_the_given_season() {
        CompetitionRepository competitions = competitions();
        Season season = new Season(2031, 2032);
        Season otherSeason = new Season(2032, 2033);
        League league = new League("Centre-Val de Loire");
        CompetitionId nationale4 = new CompetitionId(new UUID(0, 4));
        CompetitionId regionale1 = new CompetitionId(new UUID(0, 5));
        CompetitionId ofOtherSeason = new CompetitionId(new UUID(0, 6));
        competitions.save(new Competition(nationale4, "Nationale 4", league, season, 6));
        competitions.save(new Competition(regionale1, "Régionale 1", league, season, 4));
        competitions.save(new Competition(ofOtherSeason, "Nationale 4", league, otherSeason, 6));

        List<CompetitionId> found = competitions.inSeason(season).stream().map(Competition::id).toList();

        assertEquals(Set.of(nationale4, regionale1), Set.copyOf(found));
        assertEquals(2, found.size());
    }

    @Test
    void should_find_the_league_that_organizes_a_saved_competition() {
        CompetitionRepository competitions = competitions();
        CompetitionId id = new CompetitionId(new UUID(0, 3));
        competitions.save(new Competition(id, "Nationale 4 Île-de-France", new League("Île-de-France"),
                new Season(2026, 2027), 8));

        Competition found = competitions.find(id).orElseThrow();

        assertEquals(new League("Île-de-France"), found.organizingLeague());
    }
}
