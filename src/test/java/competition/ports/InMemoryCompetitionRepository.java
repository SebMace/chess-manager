package competition.ports;

import competition.domain.Competition;
import competition.domain.CompetitionId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryCompetitionRepository implements CompetitionRepository {
    private final Map<CompetitionId, Competition> competitions = new HashMap<>();

    public Optional<Competition> find(CompetitionId id) { return Optional.ofNullable(competitions.get(id)); }

    public void save(Competition competition) { competitions.put(competition.id(), competition); }
}
