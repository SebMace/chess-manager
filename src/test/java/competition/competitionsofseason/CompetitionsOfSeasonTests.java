package competition.competitionsofseason;

import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.League;
import competition.domain.Season;
import competition.ports.InMemoryCompetitionRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompetitionsOfSeasonTests {
    private static final Season CURRENT_SEASON = new Season(2026, 2027);
    private static final Season PAST_SEASON = new Season(2025, 2026);
    private static final League CENTRE_VAL_DE_LOIRE = new League("Centre-Val de Loire");
    private final InMemoryCompetitionRepository competitions = new InMemoryCompetitionRepository();
    private final CompetitionsOfSeason competitionsOfSeason = new CompetitionsOfSeason(competitions, CURRENT_SEASON);
    private int created;

    @Test
    void should_show_the_competitions_of_the_current_season_under_that_season() {
        create("Nationale 4", CENTRE_VAL_DE_LOIRE, CURRENT_SEASON);
        create("Nationale 4", CENTRE_VAL_DE_LOIRE, PAST_SEASON);

        SeasonCompetitions seen = competitionsOfSeason.competitionsOfCurrentSeason();

        assertEquals(CURRENT_SEASON, seen.season());
        assertEquals(List.of("Nationale 4"), seen.competitions().stream().map(Competition::name).toList());
        assertEquals(CURRENT_SEASON, seen.competitions().get(0).season());
    }

    @Test
    void should_show_the_competitions_in_the_order_of_their_names() {
        create("Régionale 1", CENTRE_VAL_DE_LOIRE, CURRENT_SEASON);
        create("Nationale 4", CENTRE_VAL_DE_LOIRE, CURRENT_SEASON);
        create("Départementale 2", CENTRE_VAL_DE_LOIRE, CURRENT_SEASON);

        SeasonCompetitions seen = competitionsOfSeason.competitionsOfCurrentSeason();

        assertEquals(List.of("Départementale 2", "Nationale 4", "Régionale 1"),
                seen.competitions().stream().map(Competition::name).toList());
    }

    private void create(String name, League league, Season season) {
        competitions.save(new Competition(new CompetitionId(new UUID(0, ++created)), name, league, season, 6));
    }
}
