package acceptance.steps;

import application.person.RecordPerson;
import application.person.UpdatePerson;
import application.club.RegisterLicense;
import domain.club.vo.ClubId;
import domain.club.vo.Season;
import domain.exceptions.FideIdAlreadyAssignedException;
import domain.member.vo.FfeId;
import domain.member.vo.FfeLicense;
import domain.member.vo.FfeLicenseType;
import domain.person.Person;
import domain.person.vo.PersonId;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import person.InMemoryPersonRepository;
import relationship.InMemoryClubRelationshipRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PersonIdentitySteps {
    private static final UUID FIRST_ID = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private static final UUID SECOND_ID = UUID.fromString("00000000-0000-0000-0000-000000000012");
    private final InMemoryPersonRepository people = new InMemoryPersonRepository();
    private final InMemoryClubRelationshipRepository relationships = new InMemoryClubRelationshipRepository();
    private final RecordPerson record = new RecordPerson(people);
    private final UpdatePerson update = new UpdatePerson(people);
    private PersonId id;
    private UUID secondId = SECOND_ID;
    private String firstName = "Camille";
    private String lastName = "Martin";
    private String secondFirstName = "Camille";
    private String secondLastName = "Martin";
    private Person firstRepresentation;
    private Person secondRepresentation;
    private boolean licensed;
    private Exception rejection;

    @Given("a licensed player with a supplied internal identity")
    @Given("a player has an FFE identifier and an A license")
    public void licensedPerson() { licensed = true; }

    @Given("the player has no FIDE identifier")
    public void noFideIdentifier() {
        recordPerson("Camille", "Martin");
        assertTrue(person().fideId().isEmpty());
    }

    @When("the player is recorded with first name {string} and last name {string}")
    public void recordPerson(String first, String last) {
        id = record.execute(FIRST_ID, first, last);
        if (licensed) {
            new RegisterLicense(relationships, new Season(2026, 2027)).execute(id,
                    new ClubId(UUID.fromString("00000000-0000-0000-0000-000000000002")),
                    new FfeLicense(new FfeId("A12345"), FfeLicenseType.A));
        }
    }

    @When("the player is recorded")
    public void recordPerson() { recordPerson("Camille", "Martin"); }

    @Then("the supplied internal identity is retained")
    public void identityRetained() { assertEquals(new PersonId(FIRST_ID), person().id()); }

    @Then("the recorded first name is {string}")
    public void firstName(String expected) { assertEquals(expected, person().firstName()); }

    @Then("the recorded last name is {string}")
    public void lastName(String expected) { assertEquals(expected, person().lastName()); }

    @Then("the player has an internal identity")
    public void hasIdentity() { assertEquals(id, person().id()); }

    @Then("no FIDE identifier is required")
    public void optionalFideId() { assertTrue(person().fideId().isEmpty()); }

    @When("a player is created without an internal identity")
    @When("an internal identity is created without a UUID")
    public void missingIdentity() {
        rejection = assertThrows(IllegalArgumentException.class, () -> record.execute(null, "Camille", "Martin"));
    }

    @Then("creation is rejected")
    @Then("the identity is rejected")
    @Then("the FIDE identifier is rejected")
    @Then("the rating is rejected")
    public void rejected() { assertNotNull(rejection); }

    @Given("two player representations have the same internal identity")
    public void equalIdentities() { secondId = FIRST_ID; }

    @Given("they have different names")
    public void differentNames() { secondFirstName = "Claude"; secondLastName = "Durand"; }

    @Given("two players are both named {string}")
    public void homonyms(String name) {
        String[] parts = name.split(" ", 2);
        firstName = secondFirstName = parts[0];
        lastName = secondLastName = parts[1];
    }

    @Given("they have different internal identities")
    public void differentIdentities() { secondId = SECOND_ID; }

    @When("their identities are compared")
    public void compareIdentities() {
        firstRepresentation = people.find(record.execute(FIRST_ID, firstName, lastName)).orElseThrow();
        secondRepresentation = people.find(record.execute(secondId, secondFirstName, secondLastName)).orElseThrow();
    }

    @Then("they represent the same person")
    public void samePerson() { assertEquals(firstRepresentation, secondRepresentation); }

    @Then("they represent different people")
    public void differentPeople() { assertNotEquals(firstRepresentation, secondRepresentation); }

    @Given("a player has FIDE identifier {long}")
    public void initialFide(long value) throws FideIdAlreadyAssignedException {
        recordPerson();
        update.registerFideId(id, value);
    }

    @When("FIDE identifier {long} is assigned to that player")
    public void replaceFide(long value) {
        rejection = assertThrows(FideIdAlreadyAssignedException.class, () -> update.registerFideId(id, value));
    }

    @Then("the assignment is rejected")
    public void assignmentRejected() {
        assertNotNull(rejection);
        assertEquals(641839L, person().fideId().orElseThrow().fideId());
    }

    @When("a FIDE identifier is created with value {long}")
    public void invalidFide(long value) {
        recordPerson();
        rejection = assertThrows(IllegalArgumentException.class, () -> update.registerFideId(id, value));
        assertTrue(person().fideId().isEmpty());
    }

    @Given("a player has an Elo rating of {int}")
    public void initialRating(int rating) { recordPerson(); update.recordEloRating(id, rating); }

    @When("an Elo rating of {int} is recorded")
    public void recordRating(int rating) { update.recordEloRating(id, rating); }

    @Then("the player's Elo rating is {int}")
    public void rating(int expected) { assertEquals(expected, person().eloRating().rating()); }

    @When("an Elo rating of {int} is created")
    public void invalidRating(int rating) {
        recordPerson();
        rejection = assertThrows(IllegalArgumentException.class, () -> update.recordEloRating(id, rating));
        assertNull(person().eloRating());
    }

    private Person person() { return people.find(id).orElseThrow(); }
}
