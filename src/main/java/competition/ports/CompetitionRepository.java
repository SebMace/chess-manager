package competition.ports;

import competition.domain.Competition;
import competition.domain.CompetitionId;

import java.util.Optional;

public interface CompetitionRepository {
    Optional<Competition> find(CompetitionId competitionId);
    void save(Competition competition);
}
