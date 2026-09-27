package clubmanagement.persistence;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.ports.ClubCalendar;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.Set;

public class JdbcClubCalendar implements ClubCalendar {
    private final JdbcClient jdbc;

    public JdbcClubCalendar(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void defineOpeningHours(ClubId club, Set<Session> sessions) {
    }

    @Override
    public Set<Session> openingHoursOf(ClubId club) {
        return Set.of();
    }
}
