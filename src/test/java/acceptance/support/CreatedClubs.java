package acceptance.support;

import clubmanagement.domain.club.vo.ClubId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** The clubs created in one scenario, shared by its step classes through PicoContainer. */
public class CreatedClubs {
    private final Map<String, ClubId> ids = new HashMap<>();

    public void add(String name, ClubId id) { ids.put(name, id); }

    public Optional<ClubId> idOf(String name) { return Optional.ofNullable(ids.get(name)); }
}
