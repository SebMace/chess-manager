package competition.createcompetition;

import competition.domain.CompetitionId;
import competition.domain.Season;
import competition.ports.CompetitionRepository;

import java.util.function.Supplier;

/** Compile-only skeleton: its behavior is driven by CreateCompetitionTests. */
public class CreateCompetition {
    public CreateCompetition(CompetitionRepository competitions, Season currentSeason, Supplier<CompetitionId> newCompetitionId) {
    }

    public CompetitionId createCompetition(String name, int boards) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
