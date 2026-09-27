package acceptance.steps;

import acceptance.support.CreatedClubs;
import clubmanagement.defineopeninghours.DefineOpeningHours;
import clubmanagement.domain.club.vo.Activity;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.domain.club.vo.Venue;
import clubmanagement.ports.InMemoryClubCalendar;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefineOpeningHoursSteps {
    private final CreatedClubs createdClubs;
    private final InMemoryClubCalendar clubCalendar = new InMemoryClubCalendar();
    private final DefineOpeningHours defineOpeningHours = new DefineOpeningHours(clubCalendar);

    public DefineOpeningHoursSteps(CreatedClubs createdClubs) {
        this.createdClubs = createdClubs;
    }

    @ParameterType("Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday")
    public DayOfWeek day(String day) { return DayOfWeek.valueOf(day.toUpperCase(Locale.ROOT)); }

    @ParameterType("\\d{2}:\\d{2}")
    public LocalTime time(String time) { return LocalTime.parse(time); }

    @ParameterType("[^:]+")
    public Activity activity(String name) { return new Activity(name); }

    @When("an administrator defines that {string} opens every {day} from {time} to {time}")
    public void defineOpeningHours(String club, DayOfWeek day, LocalTime from, LocalTime to) {
        defineOpeningHours.execute(clubId(club), Set.of(new Session(day, from, to)));
    }

    @Then("{string} opens every {day} from {time} to {time}")
    public void clubOpens(String club, DayOfWeek day, LocalTime from, LocalTime to) {
        assertEquals(Set.of(new Session(day, from, to)), clubCalendar.openingHoursOf(clubId(club)));
    }

    @When("an administrator defines that {string} opens every {day} from {time} to {time} for {activity}")
    public void defineOpeningHoursForActivity(String club, DayOfWeek day, LocalTime from, LocalTime to, Activity activity) {
        defineOpeningHours.execute(clubId(club), Set.of(new Session(day, from, to, Optional.of(activity))));
    }

    @Then("{string} opens every {day} from {time} to {time} for {activity}")
    public void clubOpensForActivity(String club, DayOfWeek day, LocalTime from, LocalTime to, Activity activity) {
        assertEquals(Set.of(new Session(day, from, to, Optional.of(activity))), clubCalendar.openingHoursOf(clubId(club)));
    }

    @When("an administrator defines the opening hours of {string}:")
    public void defineOpeningHoursWithSessions(String club, DataTable sessions) {
        defineOpeningHours.execute(clubId(club), sessions(sessions));
    }

    @Then("{string} opens during these sessions:")
    public void clubOpensDuringSessions(String club, DataTable sessions) {
        assertEquals(sessions(sessions), clubCalendar.openingHoursOf(clubId(club)));
    }

    @Then("the session of {string} every {day} from {time} to {time} takes place at its playing venue")
    public void sessionAtPlayingVenue(String club, DayOfWeek day, LocalTime from, LocalTime to) {
        Session session = clubCalendar.openingHoursOf(clubId(club)).stream()
                .filter(defined -> defined.day() == day && defined.from().equals(from) && defined.to().equals(to))
                .findFirst().orElseThrow();
        assertEquals(Venue.PLAYING_VENUE, session.venue());
    }

    private Set<Session> sessions(DataTable sessions) {
        return sessions.asMaps().stream().map(this::session).collect(Collectors.toSet());
    }

    private Session session(Map<String, String> session) {
        return new Session(day(session.get("day")), time(session.get("from")), time(session.get("to")),
                Optional.ofNullable(session.get("activity")).map(Activity::new));
    }

    private ClubId clubId(String club) { return createdClubs.idOf(club).orElseThrow(); }
}
