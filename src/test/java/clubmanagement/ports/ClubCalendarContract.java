package clubmanagement.ports;

import clubmanagement.domain.club.vo.Activity;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** What every ClubCalendar promises; each implementation runs these tests against itself. */
public abstract class ClubCalendarContract {

    protected abstract ClubCalendar clubCalendar();

    /** A club the implementation knows, created afresh for each test. */
    protected abstract ClubId aClub();

    @Test
    void should_read_the_session_defined_for_a_club() {
        ClubId club = aClub();
        ClubCalendar clubCalendar = clubCalendar();
        Session friday = new Session(DayOfWeek.FRIDAY, LocalTime.of(20, 0), LocalTime.of(22, 0));

        clubCalendar.defineOpeningHours(club, Set.of(friday));

        assertEquals(Set.of(friday), clubCalendar.openingHoursOf(club));
    }

    @Test
    void should_read_the_activity_of_a_session() {
        ClubId club = aClub();
        ClubCalendar clubCalendar = clubCalendar();
        Session freePlay = new Session(DayOfWeek.FRIDAY, LocalTime.of(20, 0), LocalTime.of(22, 0),
                Optional.of(new Activity("free play")));

        clubCalendar.defineOpeningHours(club, Set.of(freePlay));

        assertEquals(Set.of(freePlay), clubCalendar.openingHoursOf(club));
    }
}
