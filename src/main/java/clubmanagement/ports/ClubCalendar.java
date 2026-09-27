package clubmanagement.ports;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;

import java.util.Set;

public interface ClubCalendar {
    void defineOpeningHours(ClubId club, Set<Session> sessions);
    Set<Session> openingHoursOf(ClubId club);
}
