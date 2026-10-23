package competition.competitionsofseason;

import competition.domain.Competition;
import competition.domain.Season;
import competition.ports.CompetitionRepository;

import java.util.Comparator;
import java.util.List;

public class CompetitionsOfSeason {
    private final CompetitionRepository competitions;
    private final Season currentSeason;

    public CompetitionsOfSeason(CompetitionRepository competitions, Season currentSeason) {
        this.competitions = competitions;
        this.currentSeason = currentSeason;
    }

    public SeasonCompetitions competitionsOfCurrentSeason() {
        List<Competition> ofSeason = competitions.inSeason(currentSeason).stream()
                .sorted(Comparator.comparing(Competition::name))
                .toList();
        return new SeasonCompetitions(currentSeason, ofSeason);
    }
}
