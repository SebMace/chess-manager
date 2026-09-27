package clubmanagement.openinghoursofclub;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.ports.InMemoryClubCalendar;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpeningHoursOfClubTests {
    private static final ClubId CLUB = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    private final InMemoryClubCalendar clubCalendar = new InMemoryClubCalendar();
    private final OpeningHoursOfClub openingHoursOfClub = new OpeningHoursOfClub(clubCalendar);

    @Test
    void should_show_the_sessions_defined_for_the_club() {
        Set<Session> sessions = Set.of(new Session(DayOfWeek.FRIDAY, LocalTime.of(20, 0), LocalTime.of(22, 0)));
        clubCalendar.defineOpeningHours(CLUB, sessions);

        assertEquals(sessions, openingHoursOfClub.execute(CLUB));
    }
}
