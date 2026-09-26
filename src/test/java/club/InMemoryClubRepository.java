package club;

import clubmanagement.ports.ClubRepository;
import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.FfeClubId;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public class InMemoryClubRepository implements ClubRepository {
    private final Map<ClubId, Club> clubs = new HashMap<>();
    public Optional<Club> find(ClubId id) { return Optional.ofNullable(clubs.get(id)); }
    public void save(Club club) { clubs.put(club.id(), club); }
    public boolean existsWithFfeClubId(FfeClubId ffeClubId) {
        return clubs.values().stream().anyMatch(club -> club.ffeClubId().filter(ffeClubId::equals).isPresent());
    }
}
