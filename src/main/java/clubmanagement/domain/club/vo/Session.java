package clubmanagement.domain.club.vo;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

public record Session(DayOfWeek day, LocalTime from, LocalTime to, Optional<Activity> activity) {

    public Session(DayOfWeek day, LocalTime from, LocalTime to) {
        this(day, from, to, Optional.empty());
    }

    public Venue venue() {
        return Venue.PLAYING_VENUE;
    }
}
