package clubmanagement.ports;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryClubCalendar implements ClubCalendar {
    private final Map<ClubId, List<Session>> openingHours = new HashMap<>();
    public void defineOpeningHours(ClubId club, Session session) {
        openingHours.computeIfAbsent(club, ignored -> new ArrayList<>()).add(session);
    }
    public List<Session> openingHoursOf(ClubId club) { return List.copyOf(openingHours.getOrDefault(club, List.of())); }
}
