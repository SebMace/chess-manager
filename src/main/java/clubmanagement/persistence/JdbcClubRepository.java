package clubmanagement.persistence;

import clubmanagement.ports.ClubRepository;
import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.club.vo.FfeClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.commune.CommuneCode;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JdbcClubRepository implements ClubRepository {
    private final JdbcClient jdbc;

    public JdbcClubRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Club> find(ClubId clubId) {
        return jdbc.sql("""
                        SELECT id, name, managed_by_application, committee_code, ffe_club_id, commune_code,
                               registered_office_street, registered_office_postcode, registered_office_town,
                               playing_venue_street, playing_venue_postcode, playing_venue_town
                        FROM club WHERE id = :id""")
                .param("id", clubId.clubId())
                .query((row, rowNumber) -> new Club(
                        new ClubId(row.getObject("id", UUID.class)),
                        row.getString("name"),
                        row.getBoolean("managed_by_application"),
                        committee(row.getString("committee_code")),
                        ffeClubId(row.getString("ffe_club_id")),
                        new CommuneCode(row.getString("commune_code")),
                        new PostalAddress(row.getString("registered_office_street"),
                                row.getString("registered_office_postcode"),
                                row.getString("registered_office_town")),
                        new PostalAddress(row.getString("playing_venue_street"),
                                row.getString("playing_venue_postcode"),
                                row.getString("playing_venue_town"))))
                .optional();
    }

    @Override
    public void save(Club club) {
        jdbc.sql("""
                        INSERT INTO club (id, name, managed_by_application, committee_code, ffe_club_id, commune_code,
                                          registered_office_street, registered_office_postcode, registered_office_town,
                                          playing_venue_street, playing_venue_postcode, playing_venue_town)
                        VALUES (:id, :name, :managed, :committee, :ffeClubId, :commune,
                                :officeStreet, :officePostcode, :officeTown,
                                :venueStreet, :venuePostcode, :venueTown)""")
                .param("id", club.id().clubId())
                .param("name", club.name())
                .param("managed", club.managedByApplication())
                .param("committee", club.committee().map(CommitteeCode::value).orElse(null))
                .param("ffeClubId", club.ffeClubId().map(FfeClubId::value).orElse(null))
                .param("commune", club.commune().value())
                .param("officeStreet", club.registeredOffice().street())
                .param("officePostcode", club.registeredOffice().postcode())
                .param("officeTown", club.registeredOffice().town())
                .param("venueStreet", club.playingVenue().street())
                .param("venuePostcode", club.playingVenue().postcode())
                .param("venueTown", club.playingVenue().town())
                .update();
    }

    @Override
    public boolean existsWithFfeClubId(FfeClubId ffeClubId) {
        return jdbc.sql("SELECT EXISTS (SELECT 1 FROM club WHERE ffe_club_id = :ffeClubId)")
                .param("ffeClubId", ffeClubId.value())
                .query(Boolean.class)
                .single();
    }

    @Override
    public List<Club> inCommittee(CommitteeCode committee) {
        return jdbc.sql("""
                        SELECT id, name, managed_by_application, committee_code, ffe_club_id, commune_code,
                               registered_office_street, registered_office_postcode, registered_office_town,
                               playing_venue_street, playing_venue_postcode, playing_venue_town
                        FROM club WHERE committee_code = :committee""")
                .param("committee", committee.value())
                .query((row, rowNumber) -> new Club(
                        new ClubId(row.getObject("id", UUID.class)),
                        row.getString("name"),
                        row.getBoolean("managed_by_application"),
                        committee(row.getString("committee_code")),
                        ffeClubId(row.getString("ffe_club_id")),
                        new CommuneCode(row.getString("commune_code")),
                        new PostalAddress(row.getString("registered_office_street"),
                                row.getString("registered_office_postcode"),
                                row.getString("registered_office_town")),
                        new PostalAddress(row.getString("playing_venue_street"),
                                row.getString("playing_venue_postcode"),
                                row.getString("playing_venue_town"))))
                .list();
    }

    private static CommitteeCode committee(String code) {
        return code == null ? null : new CommitteeCode(code);
    }

    private static FfeClubId ffeClubId(String id) {
        return id == null ? null : new FfeClubId(id);
    }
}
