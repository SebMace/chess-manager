package acceptance.steps;

import application.club.CreateClub;
import club.InMemoryClubRepository;
import domain.club.Club;
import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CreateClubSteps {
    private static final UUID CREATED_CLUB_ID = UUID.fromString("00000000-0000-0000-0000-000000000006");
    private final InMemoryClubRepository clubs = new InMemoryClubRepository();
    private final CreateClub createClub = new CreateClub(clubs, () -> new ClubId(CREATED_CLUB_ID));
    private final Map<String, ClubId> createdClubs = new HashMap<>();
    private final Map<String, CommitteeCode> committees = new HashMap<>();

    @Given("{string} is a departmental committee of the FFE")
    public void departmentalCommittee(String name) { committees.put(name, new CommitteeCode("45")); }

    @When("an administrator creates the club {string}")
    public void createClub(String name) { createdClubs.put(name, createClub.execute(name)); }

    @When("an administrator creates the club {string} in the departmental committee {string}")
    public void createClubInCommittee(String name, String committee) {
        createdClubs.put(name, createClub.execute(name, committees.get(committee)));
    }

    @Then("{string} belongs to the departmental committee {string}")
    public void clubBelongsToCommittee(String name, String committee) {
        Club club = clubs.find(createdClubs.get(name)).orElseThrow();
        assertEquals(Optional.of(committees.get(committee)), club.committee());
    }

    @Then("{string} is a club managed by the application")
    public void clubIsManaged(String name) {
        Club club = clubs.find(createdClubs.get(name)).orElseThrow();
        assertEquals(name, club.name());
        assertTrue(club.managedByApplication());
    }
}
