package clubmanagement.ports;

import clubmanagement.domain.club.vo.ClubId;

import java.util.UUID;

class InMemoryClubCalendarTests extends ClubCalendarContract {
    private final InMemoryClubCalendar clubCalendar = new InMemoryClubCalendar();
    private long createdClubs;

    @Override
    protected ClubCalendar clubCalendar() { return clubCalendar; }

    @Override
    protected ClubId aClub() { return new ClubId(new UUID(0, ++createdClubs)); }
}
