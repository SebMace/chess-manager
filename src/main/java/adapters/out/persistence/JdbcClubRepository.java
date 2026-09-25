package adapters.out.persistence;

import application.club.ClubRepository;
import domain.club.Club;
import domain.club.vo.ClubId;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.Optional;

public class JdbcClubRepository implements ClubRepository {
    private final JdbcClient jdbc;

    public JdbcClubRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Club> find(ClubId clubId) {
        return Optional.empty();
    }

    @Override
    public void save(Club club) {
    }
}
