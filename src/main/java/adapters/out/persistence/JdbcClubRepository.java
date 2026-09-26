package adapters.out.persistence;

import application.club.ClubRepository;
import domain.club.Club;
import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import domain.club.vo.FfeClubId;
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
        return jdbc.sql("SELECT id, name, managed_by_application, committee_code, ffe_club_id, commune FROM club WHERE id = :id")
                .param("id", clubId.clubId())
                .query((row, rowNumber) -> new Club(
                        new ClubId(row.getObject("id", UUID.class)),
                        row.getString("name"),
                        row.getBoolean("managed_by_application"),
                        committee(row.getString("committee_code")),
                        ffeClubId(row.getString("ffe_club_id")),
                        row.getString("commune")))
                .optional();
    }

    @Override
    public void save(Club club) {
        jdbc.sql("""
                        INSERT INTO club (id, name, managed_by_application, committee_code, ffe_club_id, commune)
                        VALUES (:id, :name, :managed, :committee, :ffeClubId, :commune)""")
                .param("id", club.id().clubId())
                .param("name", club.name())
                .param("managed", club.managedByApplication())
                .param("committee", club.committee().map(CommitteeCode::value).orElse(null))
                .param("ffeClubId", club.ffeClubId().map(FfeClubId::value).orElse(null))
                .param("commune", club.commune().orElse(null))
                .update();
    }

    @Override
    public boolean existsWithFfeClubId(FfeClubId ffeClubId) {
        return jdbc.sql("SELECT EXISTS (SELECT 1 FROM club WHERE ffe_club_id = :ffeClubId)")
                .param("ffeClubId", ffeClubId.value())
                .query(Boolean.class)
                .single();
    }

    private static CommitteeCode committee(String code) {
        return code == null ? null : new CommitteeCode(code);
    }

    private static FfeClubId ffeClubId(String id) {
        return id == null ? null : new FfeClubId(id);
    }
}
