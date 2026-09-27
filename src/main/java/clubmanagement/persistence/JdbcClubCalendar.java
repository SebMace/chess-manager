package clubmanagement.persistence;

import clubmanagement.domain.club.vo.Activity;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.ports.ClubCalendar;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JdbcClubCalendar implements ClubCalendar {
    private final JdbcClient jdbc;

    public JdbcClubCalendar(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void defineOpeningHours(ClubId club, Set<Session> sessions) {
        sessions.forEach(session -> jdbc.sql("""
                        INSERT INTO club_session (club_id, day_of_week, starts_at, ends_at, activity)
                        VALUES (:club, :day, :from, :to, :activity)""")
                .param("club", club.clubId())
                .param("day", session.day().name())
                .param("from", session.from())
                .param("to", session.to())
                .param("activity", session.activity().map(Activity::name).orElse(null))
                .update());
    }

    @Override
    public Set<Session> openingHoursOf(ClubId club) {
        return jdbc.sql("SELECT day_of_week, starts_at, ends_at, activity FROM club_session WHERE club_id = :club")
                .param("club", club.clubId())
                .query(JdbcClubCalendar::session)
                .stream()
                .collect(Collectors.toSet());
    }

    private static Session session(ResultSet row, int rowNumber) throws SQLException {
        return new Session(DayOfWeek.valueOf(row.getString("day_of_week")),
                row.getObject("starts_at", LocalTime.class),
                row.getObject("ends_at", LocalTime.class),
                Optional.ofNullable(row.getString("activity")).map(Activity::new));
    }
}
