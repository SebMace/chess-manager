package competition.createcompetition;

import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.League;
import competition.domain.Season;
import competition.ports.CompetitionRepository;

import java.util.function.Supplier;

public class CreateCompetition {
    private final CompetitionRepository competitions;
    private final Season currentSeason;
    private final Supplier<CompetitionId> newCompetitionId;

    public CreateCompetition(CompetitionRepository competitions, Season currentSeason, Supplier<CompetitionId> newCompetitionId) {
        this.competitions = competitions;
        this.currentSeason = currentSeason;
        this.newCompetitionId = newCompetitionId;
    }

    public CompetitionId createCompetition(String name, League league, int boards) {
        CompetitionId id = newCompetitionId.get();
        competitions.save(new Competition(id, name, currentSeason, boards));
        return id;
    }
}
