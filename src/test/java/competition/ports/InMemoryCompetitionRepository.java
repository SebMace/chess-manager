package competition.ports;

import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.Season;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryCompetitionRepository implements CompetitionRepository {
    private final Map<CompetitionId, Competition> competitions = new HashMap<>();

    public Optional<Competition> find(CompetitionId id) { return Optional.ofNullable(competitions.get(id)); }

    public List<Competition> inSeason(Season season) {
        return competitions.values().stream().filter(competition -> competition.season().equals(season)).toList();
    }

    public void save(Competition competition) { competitions.put(competition.id(), competition); }
}
