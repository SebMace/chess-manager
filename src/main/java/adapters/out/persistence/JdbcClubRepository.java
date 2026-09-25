package adapters.out.persistence;

import application.club.ClubRepository;
import domain.club.Club;
import domain.club.vo.ClubId;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.Optional;
import java.util.UUID;

public class JdbcClubRepository implements ClubRepository {
    private final JdbcClient jdbc;

    public JdbcClubRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Club> find(ClubId clubId) {
        return jdbc.sql("SELECT id, name, managed_by_application FROM club WHERE id = :id")
                .param("id", clubId.clubId())
                .query((row, rowNumber) -> new Club(
                        new ClubId(row.getObject("id", UUID.class)),
                        row.getString("name"),
                        row.getBoolean("managed_by_application")))
                .optional();
    }

    @Override
    public void save(Club club) {
        jdbc.sql("INSERT INTO club (id, name, managed_by_application) VALUES (:id, :name, :managed)")
                .param("id", club.id().clubId())
                .param("name", club.name())
                .param("managed", club.managedByApplication())
                .update();
    }
}
