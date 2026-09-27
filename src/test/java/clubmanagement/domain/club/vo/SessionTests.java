package clubmanagement.domain.club.vo;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionTests {

    @Test
    void should_take_place_at_the_playing_venue_of_the_club_by_default() {
        Session session = new Session(DayOfWeek.FRIDAY, LocalTime.of(20, 0), LocalTime.of(22, 0));

        assertEquals(Venue.PLAYING_VENUE, session.venue());
    }
}
