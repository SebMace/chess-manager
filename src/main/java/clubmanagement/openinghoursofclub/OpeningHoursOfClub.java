package clubmanagement.openinghoursofclub;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.ports.ClubCalendar;

import java.util.Set;

/** The sessions during which a club opens. */
public class OpeningHoursOfClub {
    private final ClubCalendar clubCalendar;

    public OpeningHoursOfClub(ClubCalendar clubCalendar) {
        this.clubCalendar = clubCalendar;
    }

    public Set<Session> execute(ClubId club) {
        return clubCalendar.openingHoursOf(club);
    }
}
