package club;

import application.club.ClubRepository;
import domain.club.Club;
import domain.club.vo.ClubId;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public class InMemoryClubRepository implements ClubRepository {
    private final Map<ClubId, Club> clubs = new HashMap<>();
    public Optional<Club> find(ClubId id) { return Optional.ofNullable(clubs.get(id)); }
    public void save(Club club) { clubs.put(club.id(), club); }
}
