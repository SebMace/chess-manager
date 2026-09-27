package clubmanagement.defineopeninghours;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.ports.ClubCalendar;

import java.util.Set;

public class DefineOpeningHours {
    private final ClubCalendar clubCalendar;

    public DefineOpeningHours(ClubCalendar clubCalendar) {
        this.clubCalendar = clubCalendar;
    }

    public void execute(ClubId club, Set<Session> sessions) {
        clubCalendar.defineOpeningHours(club, sessions);
    }
}
