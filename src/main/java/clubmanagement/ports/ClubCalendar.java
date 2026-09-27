package clubmanagement.ports;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;

import java.util.List;

public interface ClubCalendar {
    void defineOpeningHours(ClubId club, Session session);
    List<Session> openingHoursOf(ClubId club);
}
