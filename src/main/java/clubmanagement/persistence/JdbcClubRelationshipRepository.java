package clubmanagement.persistence;

import clubmanagement.domain.club.ClubRelationship;
import clubmanagement.domain.club.RelationshipStatus;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.ports.ClubRelationshipRepository;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Only what registering a member needs is implemented. The relationship and its licenses are written by
 * separate statements: the use case that saves them runs in a transaction.
 */
public class JdbcClubRelationshipRepository implements ClubRelationshipRepository {
    private record License(Season season, FfeLicenseType type) {}

    private final JdbcClient jdbc;

    public JdbcClubRelationshipRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<ClubRelationship> find(PersonId personId, ClubId clubId) {
        return jdbc.sql("SELECT status FROM club_relationship WHERE person_id = :person AND club_id = :club")
                .param("person", personId.personId())
                .param("club", clubId.clubId())
                .query(String.class)
                .optional()
                .map(status -> relationship(personId, clubId, RelationshipStatus.valueOf(status), licenses(personId, clubId)));
    }

    @Override
    public void save(ClubRelationship relationship) {
        jdbc.sql("INSERT INTO club_relationship (person_id, club_id, status) VALUES (:person, :club, :status)")
                .param("person", relationship.personId().personId())
                .param("club", relationship.clubId().clubId())
                .param("status", relationship.status().name())
                .update();
        relationship.licenses().forEach((season, type) -> jdbc.sql("""
                        INSERT INTO club_relationship_license
                            (person_id, club_id, season_begin_year, season_end_year, license_type)
                        VALUES (:person, :club, :beginYear, :endYear, :type)""")
                .param("person", relationship.personId().personId())
                .param("club", relationship.clubId().clubId())
                .param("beginYear", season.yearBegin())
                .param("endYear", season.yearEnd())
                .param("type", type.name())
                .update());
    }

    @Override
    public List<ClubRelationship> findByPerson(PersonId personId) {
        throw new UnsupportedOperationException("No use case wired to PostgreSQL looks up the relationships of a person");
    }

    @Override
    public void delete(PersonId personId, ClubId clubId) {
        throw new UnsupportedOperationException("No use case wired to PostgreSQL deletes a relationship");
    }

    private List<License> licenses(PersonId personId, ClubId clubId) {
        return jdbc.sql("""
                        SELECT season_begin_year, season_end_year, license_type FROM club_relationship_license
                        WHERE person_id = :person AND club_id = :club""")
                .param("person", personId.personId())
                .param("club", clubId.clubId())
                .query(JdbcClubRelationshipRepository::license)
                .list();
    }

    /** Replays the stored licenses and status through the domain's own operations: no state is built behind its back. */
    private static ClubRelationship relationship(PersonId personId, ClubId clubId, RelationshipStatus status,
                                                 List<License> licenses) {
        ClubRelationship relationship = new ClubRelationship(personId, clubId);
        for (License license : licenses) {
            relationship = relationship.registerLicense(license.type(), license.season());
        }
        return status == RelationshipStatus.PARTNER ? relationship.registerPartnership() : relationship;
    }

    private static License license(ResultSet row, int rowNumber) throws SQLException {
        return new License(new Season(row.getInt("season_begin_year"), row.getInt("season_end_year")),
                FfeLicenseType.valueOf(row.getString("license_type")));
    }
}
