package clubmanagement.defineopeninghours;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.ports.InMemoryClubCalendar;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefineOpeningHoursTests {
    private static final ClubId CLUB = new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    private final InMemoryClubCalendar clubCalendar = new InMemoryClubCalendar();
    private final DefineOpeningHours defineOpeningHours = new DefineOpeningHours(clubCalendar);

    @Test
    void should_open_the_club_during_the_opening_hours_defined_for_it() {
        Session fridayAfternoon = new Session(DayOfWeek.FRIDAY, LocalTime.of(15, 0), LocalTime.of(17, 0));

        defineOpeningHours.execute(CLUB, fridayAfternoon);

        assertEquals(List.of(fridayAfternoon), clubCalendar.openingHoursOf(CLUB));
    }
}
