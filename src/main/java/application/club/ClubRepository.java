package application.club;

import domain.club.Club;
import domain.club.vo.ClubId;
import java.util.Optional;

public interface
ClubRepository {
    Optional<Club> find(ClubId clubId);
    void save(Club club);
}
