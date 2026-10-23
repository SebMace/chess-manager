package clubmanagement.persistence;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.EloRating;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.member.vo.FideTitle;
import clubmanagement.ports.ClubMember;
import clubmanagement.ports.ClubMembers;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JdbcClubMembers implements ClubMembers {
    private final JdbcClient jdbc;

    public JdbcClubMembers(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<ClubMember> ofClub(ClubId club, Season season) {
        return jdbc.sql("""
                        SELECT p.first_name, p.last_name, p.ffe_id, p.elo, p.fide_title, l.license_type
                        FROM club_relationship_license l JOIN person p ON p.id = l.person_id
                        WHERE l.club_id = :club AND l.season_begin_year = :beginYear AND l.season_end_year = :endYear
                        ORDER BY p.last_name, p.first_name""")
                .param("club", club.clubId())
                .param("beginYear", season.yearBegin())
                .param("endYear", season.yearEnd())
                .query(JdbcClubMembers::member)
                .list();
    }

    private static ClubMember member(ResultSet row, int rowNumber) throws SQLException {
        return new ClubMember(row.getString("first_name"), row.getString("last_name"),
                new FfeId(row.getString("ffe_id")), FfeLicenseType.valueOf(row.getString("license_type")),
                Optional.ofNullable(row.getObject("elo", Integer.class)).map(EloRating::new),
                Optional.ofNullable(row.getString("fide_title")).map(FideTitle::valueOf));
    }
}
