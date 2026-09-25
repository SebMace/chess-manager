package acceptance.steps;

import application.club.CreateClub;
import club.InMemoryClubRepository;
import domain.club.Club;
import domain.club.vo.ClubId;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CreateClubSteps {
    private static final UUID CREATED_CLUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000006");
    private final InMemoryClubRepository clubs = new InMemoryClubRepository();
    private final CreateClub createClub = new CreateClub(clubs, () -> new ClubId(CREATED_CLUB_ID));
    private final Map<String, ClubId> createdClubs = new HashMap<>();

    @When("an administrator creates the club {string}")
    public void createClub(String name) { createdClubs.put(name, createClub.execute(name)); }

    @Then("{string} is a club managed by the application")
    public void clubIsManaged(String name) {
        Club club = clubs.find(createdClubs.get(name)).orElseThrow();
        assertEquals(name, club.name());
        assertTrue(club.managedByApplication());
    }
}
