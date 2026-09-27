package clubmanagement.defineopeninghours;

import clubmanagement.domain.club.vo.Activity;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.ports.InMemoryClubCalendar;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefineOpeningHoursTests {
    private static final ClubId CLUB = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    private final InMemoryClubCalendar clubCalendar = new InMemoryClubCalendar();
    private final DefineOpeningHours defineOpeningHours = new DefineOpeningHours(clubCalendar);

    @Test
    void should_open_the_club_during_the_opening_hours_defined_for_it() {
        Session fridayAfternoon = new Session(DayOfWeek.FRIDAY, LocalTime.of(15, 0), LocalTime.of(17, 0));

        defineOpeningHours.execute(CLUB, Set.of(fridayAfternoon));

        assertEquals(Set.of(fridayAfternoon), clubCalendar.openingHoursOf(CLUB));
    }

    @Test
    void should_open_the_club_during_all_the_sessions_defined_at_once() {
        Set<Session> sessions = Set.of(
                new Session(DayOfWeek.FRIDAY, LocalTime.of(20, 0), LocalTime.of(22, 0), Optional.of(new Activity("free play"))),
                new Session(DayOfWeek.SATURDAY, LocalTime.of(15, 0), LocalTime.of(17, 0), Optional.of(new Activity("adult lessons"))));

        defineOpeningHours.execute(CLUB, sessions);

        assertEquals(sessions, clubCalendar.openingHoursOf(CLUB));
    }
}
