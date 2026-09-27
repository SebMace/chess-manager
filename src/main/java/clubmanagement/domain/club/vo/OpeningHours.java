package clubmanagement.domain.club.vo;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record OpeningHours(DayOfWeek day, LocalTime from, LocalTime to) {
}
