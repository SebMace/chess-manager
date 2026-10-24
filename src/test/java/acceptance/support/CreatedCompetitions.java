package acceptance.support;

import competition.domain.CompetitionId;
import competition.ports.InMemoryCompetitionRepository;

import java.util.HashMap;
import java.util.Map;

/** The competitions created in one scenario and the repository holding them, shared by its step classes through PicoContainer. */
public class CreatedCompetitions {
    private final InMemoryCompetitionRepository competitions = new InMemoryCompetitionRepository();
    private final Map<String, CompetitionId> ids = new HashMap<>();

    public InMemoryCompetitionRepository repository() { return competitions; }

    public void add(String name, CompetitionId id) { ids.put(name, id); }

    public CompetitionId idOf(String name) {
        CompetitionId id = ids.get(name);
        if (id == null) throw new IllegalStateException("No competition named " + name + " was created in this scenario");
        return id;
    }
}
