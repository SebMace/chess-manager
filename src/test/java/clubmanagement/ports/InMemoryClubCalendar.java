package clubmanagement.ports;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InMemoryClubCalendar implements ClubCalendar {
    private final Map<ClubId, Set<Session>> openingHours = new HashMap<>();
    public void defineOpeningHours(ClubId club, Set<Session> sessions) { openingHours.put(club, Set.copyOf(sessions)); }
    public Set<Session> openingHoursOf(ClubId club) { return openingHours.getOrDefault(club, Set.of()); }
}
