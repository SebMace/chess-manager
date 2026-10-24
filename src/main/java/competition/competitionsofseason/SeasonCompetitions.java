package competition.competitionsofseason;

import competition.domain.Competition;
import competition.domain.Season;

import java.util.List;

/** The competitions of a season, with that season: it is known even when no competition exists yet. */
public record SeasonCompetitions(Season season, List<Competition> competitions) {
}
