package clubmanagement.ports;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.OpeningHours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryClubCalendar implements ClubCalendar {
    private final Map<ClubId, List<OpeningHours>> openingHours = new HashMap<>();
    public void defineOpeningHours(ClubId club, OpeningHours hours) {
        openingHours.computeIfAbsent(club, ignored -> new ArrayList<>()).add(hours);
    }
    public List<OpeningHours> openingHoursOf(ClubId club) { return List.copyOf(openingHours.getOrDefault(club, List.of())); }
}
