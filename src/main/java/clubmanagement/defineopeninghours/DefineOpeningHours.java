package clubmanagement.defineopeninghours;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.ports.ClubCalendar;

public class DefineOpeningHours {
    private final ClubCalendar clubCalendar;

    public DefineOpeningHours(ClubCalendar clubCalendar) {
        this.clubCalendar = clubCalendar;
    }

    public void execute(ClubId club, Session session) {
        clubCalendar.defineOpeningHours(club, session);
    }
}
