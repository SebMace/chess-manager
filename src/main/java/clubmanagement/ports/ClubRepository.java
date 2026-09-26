package clubmanagement.ports;

import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.FfeClubId;
import java.util.Optional;

public interface
ClubRepository {
    Optional<Club> find(ClubId clubId);
    void save(Club club);
    boolean existsWithFfeClubId(FfeClubId ffeClubId);
}
