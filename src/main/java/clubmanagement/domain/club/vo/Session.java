package clubmanagement.domain.club.vo;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

public record Session(DayOfWeek day, LocalTime from, LocalTime to, Optional<Activity> activity, Venue venue) {

    public Session(DayOfWeek day, LocalTime from, LocalTime to) {
        this(day, from, to, Optional.empty());
    }

    public Session(DayOfWeek day, LocalTime from, LocalTime to, Optional<Activity> activity) {
        this(day, from, to, activity, Venue.PLAYING_VENUE);
    }
}
