package competition.persistence;

import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.League;
import competition.domain.Season;
import competition.ports.CompetitionRepository;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class JdbcCompetitionRepository implements CompetitionRepository {
    private final JdbcClient jdbc;

    public JdbcCompetitionRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Competition> find(CompetitionId competitionId) {
        return jdbc.sql("""
                        SELECT id, name, season_begin_year, season_end_year, boards, organizing_league_name
                        FROM competition WHERE id = :id""")
                .param("id", competitionId.competitionId())
                .query(JdbcCompetitionRepository::competition)
                .optional();
    }

    @Override
    public void save(Competition competition) {
        jdbc.sql("""
                        INSERT INTO competition (id, name, season_begin_year, season_end_year, boards, organizing_league_name)
                        VALUES (:id, :name, :seasonBeginYear, :seasonEndYear, :boards, :organizingLeagueName)""")
                .param("id", competition.id().competitionId())
                .param("name", competition.name())
                .param("seasonBeginYear", competition.season().yearBegin())
                .param("seasonEndYear", competition.season().yearEnd())
                .param("boards", competition.boards())
                .param("organizingLeagueName", competition.organizingLeague().name())
                .update();
    }

    private static Competition competition(ResultSet row, int rowNumber) throws SQLException {
        return new Competition(
                new CompetitionId(row.getObject("id", UUID.class)),
                row.getString("name"),
                new League(row.getString("organizing_league_name")),
                new Season(row.getInt("season_begin_year"), row.getInt("season_end_year")),
                row.getInt("boards"));
    }
}
