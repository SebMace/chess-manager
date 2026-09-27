package acceptance.steps;

import acceptance.support.CreatedClubs;
import clubmanagement.defineopeninghours.DefineOpeningHours;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.OpeningHours;
import clubmanagement.ports.InMemoryClubCalendar;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

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

    @When("an administrator defines that {string} opens every {day} from {time} to {time}")
    public void defineOpeningHours(String club, DayOfWeek day, LocalTime from, LocalTime to) {
        defineOpeningHours.execute(clubId(club), new OpeningHours(day, from, to));
    }

    @Then("{string} opens every {day} from {time} to {time}")
    public void clubOpens(String club, DayOfWeek day, LocalTime from, LocalTime to) {
        assertEquals(List.of(new OpeningHours(day, from, to)), clubCalendar.openingHoursOf(clubId(club)));
    }

    private ClubId clubId(String club) { return createdClubs.idOf(club).orElseThrow(); }
}
