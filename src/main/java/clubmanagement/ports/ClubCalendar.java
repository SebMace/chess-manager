package clubmanagement.ports;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.OpeningHours;

import java.util.List;

public interface ClubCalendar {
    void defineOpeningHours(ClubId club, OpeningHours openingHours);
    List<OpeningHours> openingHoursOf(ClubId club);
}
