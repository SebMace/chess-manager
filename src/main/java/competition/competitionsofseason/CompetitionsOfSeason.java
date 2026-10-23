package competition.competitionsofseason;

import competition.domain.Season;
import competition.ports.CompetitionRepository;

public class CompetitionsOfSeason {
    private final CompetitionRepository competitions;
    private final Season currentSeason;

    public CompetitionsOfSeason(CompetitionRepository competitions, Season currentSeason) {
        this.competitions = competitions;
        this.currentSeason = currentSeason;
    }

    public SeasonCompetitions competitionsOfCurrentSeason() {
        return new SeasonCompetitions(currentSeason, competitions.inSeason(currentSeason));
    }
}
