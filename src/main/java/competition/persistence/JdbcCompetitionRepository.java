package competition.persistence;

import competition.domain.ClubId;
import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.League;
import competition.domain.Season;
import competition.domain.Team;
import competition.ports.CompetitionRepository;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JdbcCompetitionRepository implements CompetitionRepository {
    private static final String SELECT_COMPETITIONS = """
            SELECT id, name, season_begin_year, season_end_year, boards, organizing_league_name
            FROM competition""";
    private final JdbcClient jdbc;

    public JdbcCompetitionRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Competition> find(CompetitionId competitionId) {
        return jdbc.sql(SELECT_COMPETITIONS + " WHERE id = :id")
                .param("id", competitionId.competitionId())
                .query(JdbcCompetitionRepository::row)
                .optional()
                .map(row -> row.competition(teamsOf(competitionId)));
    }

    @Override
    public List<Competition> inSeason(Season season) {
        return jdbc.sql(SELECT_COMPETITIONS + " WHERE season_begin_year = :beginYear AND season_end_year = :endYear")
                .param("beginYear", season.yearBegin())
                .param("endYear", season.yearEnd())
                .query(JdbcCompetitionRepository::row)
                .list().stream()
                .map(row -> row.competition(List.of()))
                .toList();
    }

    @Override
    public void save(Competition competition) {
        jdbc.sql("""
                        INSERT INTO competition (id, name, season_begin_year, season_end_year, boards, organizing_league_name)
                        VALUES (:id, :name, :seasonBeginYear, :seasonEndYear, :boards, :organizingLeagueName)
                        ON CONFLICT (id) DO NOTHING""")
                .param("id", competition.id().competitionId())
                .param("name", competition.name())
                .param("seasonBeginYear", competition.season().yearBegin())
                .param("seasonEndYear", competition.season().yearEnd())
                .param("boards", competition.boards())
                .param("organizingLeagueName", competition.organizingLeague().name())
                .update();
        jdbc.sql("DELETE FROM competition_team WHERE competition_id = :competition")
                .param("competition", competition.id().competitionId())
                .update();
        competition.teams().forEach(team -> jdbc.sql("""
                        INSERT INTO competition_team (competition_id, club_id, team_number)
                        VALUES (:competition, :club, :number)""")
                .param("competition", competition.id().competitionId())
                .param("club", team.club().clubId())
                .param("number", team.number())
                .update());
    }

    private List<Team> teamsOf(CompetitionId competitionId) {
        return jdbc.sql("""
                        SELECT club_id, team_number FROM competition_team
                        WHERE competition_id = :competition ORDER BY club_id, team_number""")
                .param("competition", competitionId.competitionId())
                .query((row, rowNumber) -> new Team(new ClubId(row.getObject("club_id", UUID.class)),
                        row.getInt("team_number")))
                .list();
    }

    /** What the competition table holds; the teams live in a table of their own. */
    private record CompetitionRow(CompetitionId id, String name, League organizingLeague, Season season, int boards) {
        Competition competition(List<Team> teams) {
            return Competition.reconstitute(id, name, organizingLeague, season, boards, teams);
        }
    }

    private static CompetitionRow row(ResultSet row, int rowNumber) throws SQLException {
        return new CompetitionRow(
                new CompetitionId(row.getObject("id", UUID.class)),
                row.getString("name"),
                new League(row.getString("organizing_league_name")),
                new Season(row.getInt("season_begin_year"), row.getInt("season_end_year")),
                row.getInt("boards"));
    }
}
