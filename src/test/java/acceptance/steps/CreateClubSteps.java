package acceptance.steps;

import application.club.CommuneNotInCommitteeDepartment;
import application.club.CreateClub;
import application.club.FfeClubIdAlreadyUsed;
import club.InMemoryClubRepository;
import commune.InMemoryCommunes;
import domain.club.Club;
import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import domain.club.vo.FfeClubId;
import domain.commune.CommuneCode;
import io.cucumber.datatable.DataTable;
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
    private static final Map<String, CommuneCode> COMMUNES = Map.of(
            "Orléans", new CommuneCode("45234"),
            "Olivet (Mayenne)", new CommuneCode("53169"));
    private final InMemoryClubRepository clubs = new InMemoryClubRepository();
    private final CreateClub createClub = new CreateClub(clubs, new InMemoryCommunes(), () -> new ClubId(CREATED_CLUB_ID));
    private final Map<String, ClubId> createdClubs = new HashMap<>();
    private final Map<String, CommitteeCode> committees = new HashMap<>();
    private final Map<String, RuntimeException> refusals = new HashMap<>();

    @Given("{string} is a departmental committee of the FFE")
    public void departmentalCommittee(String name) { committees.put(name, new CommitteeCode("45")); }

    @When("an administrator creates the club {string} with:")
    public void createClubWith(String name, DataTable information) {
        Map<String, String> club = information.asMap();
        String ffeIdentifier = club.get("FFE identifier");
        try {
            createdClubs.put(name, createClub.execute(name,
                    committees.get(club.get("departmental committee")),
                    ffeIdentifier == null ? null : new FfeClubId(ffeIdentifier),
                    commune(club.get("commune"))));
        } catch (IllegalArgumentException | FfeClubIdAlreadyUsed | CommuneNotInCommitteeDepartment refusal) {
            refusals.put(name, refusal);
        }
    }

    @Given("an administrator has created the club {string} with:")
    public void clubCreated(String name, DataTable information) {
        Map<String, String> club = information.asMap();
        createdClubs.put(name, createClub.execute(name, committees.get(club.get("departmental committee")),
                new FfeClubId(club.get("FFE identifier")), commune(club.get("commune"))));
    }

    @Then("the administrator is told that the FFE identifier {string} is already used")
    public void ffeIdentifierAlreadyUsed(String ffeIdentifier) {
        FfeClubIdAlreadyUsed refusal = assertInstanceOf(FfeClubIdAlreadyUsed.class, refusals.values().iterator().next());
        assertEquals(new FfeClubId(ffeIdentifier), refusal.ffeClubId());
    }

    @Then("the club {string} is not created")
    public void clubIsNotCreated(String name) {
        assertNotNull(refusals.get(name), "the creation should have been refused");
        assertNull(createdClubs.get(name));
    }

    @Then("the FFE identifier of {string} is {string}")
    public void clubFfeIdentifier(String name, String ffeIdentifier) {
        assertEquals(Optional.of(new FfeClubId(ffeIdentifier)), createdClub(name).ffeClubId());
    }

    @Then("the commune of {string} is {string}")
    public void clubCommune(String name, String commune) {
        assertEquals(commune(commune), createdClub(name).commune());
    }

    private static CommuneCode commune(String name) {
        return name == null ? null : COMMUNES.get(name);
    }

    private Club createdClub(String name) { return clubs.find(createdClubs.get(name)).orElseThrow(); }

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
