package clubmanagement.domain.club.vo;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record Session(DayOfWeek day, LocalTime from, LocalTime to) {
}
