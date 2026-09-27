package clubmanagement.persistence;

import clubmanagement.domain.club.vo.Activity;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.domain.club.vo.Venue;
import clubmanagement.ports.ClubCalendar;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.support.TransactionOperations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JdbcClubCalendar implements ClubCalendar {
    private final JdbcClient jdbc;
    private final TransactionOperations transactions;

    public JdbcClubCalendar(JdbcClient jdbc, TransactionOperations transactions) {
        this.jdbc = jdbc;
        this.transactions = transactions;
    }

    /**
     * Replaces the sessions in one transaction: a failure between the deletion and the insertions must
     * not leave the club without opening hours, or with only part of them. No test forces this failure.
     */
    @Override
    public void defineOpeningHours(ClubId club, Set<Session> sessions) {
        transactions.executeWithoutResult(transaction -> {
            jdbc.sql("DELETE FROM club_session WHERE club_id = :club").param("club", club.clubId()).update();
            sessions.forEach(session -> insert(club, session));
        });
    }

    private void insert(ClubId club, Session session) {
        Optional<PostalAddress> address = address(session.venue());
        jdbc.sql("""
                        INSERT INTO club_session (club_id, day_of_week, starts_at, ends_at, activity,
                                                  venue_street, venue_postcode, venue_town)
                        VALUES (:club, :day, :from, :to, :activity, :street, :postcode, :town)""")
                .param("club", club.clubId())
                .param("day", session.day().name())
                .param("from", session.from())
                .param("to", session.to())
                .param("activity", session.activity().map(Activity::name).orElse(null))
                .param("street", address.map(PostalAddress::street).orElse(null))
                .param("postcode", address.map(PostalAddress::postcode).orElse(null))
                .param("town", address.map(PostalAddress::town).orElse(null))
                .update();
    }

    @Override
    public Set<Session> openingHoursOf(ClubId club) {
        return jdbc.sql("""
                        SELECT day_of_week, starts_at, ends_at, activity, venue_street, venue_postcode, venue_town
                        FROM club_session WHERE club_id = :club""")
                .param("club", club.clubId())
                .query(JdbcClubCalendar::session)
                .stream()
                .collect(Collectors.toSet());
    }

    private static Session session(ResultSet row, int rowNumber) throws SQLException {
        return new Session(DayOfWeek.valueOf(row.getString("day_of_week")),
                row.getObject("starts_at", LocalTime.class),
                row.getObject("ends_at", LocalTime.class),
                Optional.ofNullable(row.getString("activity")).map(Activity::new),
                venue(row));
    }

    /** The playing venue of the club is kept as a reference: no address is stored for it. */
    private static Optional<PostalAddress> address(Venue venue) {
        return switch (venue) {
            case Venue.Address(PostalAddress address) -> Optional.of(address);
            case Venue.PlayingVenue() -> Optional.empty();
        };
    }

    private static Venue venue(ResultSet row) throws SQLException {
        String street = row.getString("venue_street");
        return street == null ? Venue.PLAYING_VENUE
                : new Venue.Address(new PostalAddress(street, row.getString("venue_postcode"), row.getString("venue_town")));
    }
}
