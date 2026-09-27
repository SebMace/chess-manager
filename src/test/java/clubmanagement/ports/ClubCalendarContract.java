package clubmanagement.ports;

import clubmanagement.domain.club.vo.Activity;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.domain.club.vo.Venue;
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

    @Test
    void should_read_the_address_where_a_session_takes_place() {
        ClubId club = aClub();
        ClubCalendar clubCalendar = clubCalendar();
        Session atSchool = new Session(DayOfWeek.MONDAY, LocalTime.of(20, 0), LocalTime.of(22, 0), Optional.empty(),
                new Venue.Address(new PostalAddress("3 rue de l'École", "45000", "Orléans")));

        clubCalendar.defineOpeningHours(club, Set.of(atSchool));

        assertEquals(Set.of(atSchool), clubCalendar.openingHoursOf(club));
    }

    @Test
    void should_replace_the_sessions_previously_defined_for_a_club() {
        ClubId club = aClub();
        ClubCalendar clubCalendar = clubCalendar();
        Session friday = new Session(DayOfWeek.FRIDAY, LocalTime.of(20, 0), LocalTime.of(22, 0));
        Session saturday = new Session(DayOfWeek.SATURDAY, LocalTime.of(15, 0), LocalTime.of(17, 0));
        clubCalendar.defineOpeningHours(club, Set.of(friday));

        clubCalendar.defineOpeningHours(club, Set.of(saturday));

        assertEquals(Set.of(saturday), clubCalendar.openingHoursOf(club));
    }
}
