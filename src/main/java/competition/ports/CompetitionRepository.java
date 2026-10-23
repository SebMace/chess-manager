package competition.ports;

import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.Season;

import java.util.List;
import java.util.Optional;

public interface CompetitionRepository {
    Optional<Competition> find(CompetitionId competitionId);
    List<Competition> inSeason(Season season);
    void save(Competition competition);
}
