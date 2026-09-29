package acceptance.steps;

import acceptance.support.CreatedClubs;
import clubmanagement.domain.club.ClubRelationship;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicense;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.person.Person;
import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.ports.InMemoryClubRelationshipRepository;
import clubmanagement.ports.InMemoryPersonRepository;
import clubmanagement.registermember.RegisterNewMember;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Optional;
import java.util.UUID;

import static clubmanagement.domain.club.RelationshipStatus.MEMBER;
import static org.junit.jupiter.api.Assertions.*;

public class RegisterMemberSteps {
    private static final PersonId REGISTERED_PLAYER_ID = new PersonId(new UUID(0, 1));
    private final CreatedClubs createdClubs;
    private final InMemoryPersonRepository people = new InMemoryPersonRepository();
    private final InMemoryClubRelationshipRepository relationships = new InMemoryClubRelationshipRepository();
    /** The player is found again by the identity the registration gave, never by their name. */
    private PersonId registeredPlayer;

    public RegisterMemberSteps(CreatedClubs createdClubs) {
        this.createdClubs = createdClubs;
    }

    /** A player's first and last name, as the administrator gives them: never used to find the player again. */
    record PlayerName(String firstName, String lastName) {}

    @ParameterType("([A-Z][\\p{L}-]*) ([A-Z][\\p{L}-]*)")
    public PlayerName player(String firstName, String lastName) { return new PlayerName(firstName, lastName); }

    @When("an administrator registers {player}, whose FFE identifier is {string}, as a member of {string} for the {int}-{int} season with a/an {word} license")
    public void administratorRegistersANewMember(PlayerName name, String ffeIdentifier,
                                                 String clubName, int yearBegin, int yearEnd, String licenseType) {
        RegisterNewMember registerNewMember = new RegisterNewMember(people, relationships, new Season(yearBegin, yearEnd),
                () -> REGISTERED_PLAYER_ID);
        registeredPlayer = registerNewMember.registerNewMember(createdClubs.idOf(clubName).orElseThrow(),
                name.firstName(), name.lastName(), new FfeId(ffeIdentifier), FfeLicenseType.valueOf(licenseType));
    }

    @Then("{player} is a member of {string} for the {int}-{int} season with a/an {word} license")
    public void registeredPlayerIsMemberOfTheClubForTheSeason(PlayerName name, String clubName,
                                                             int yearBegin, int yearEnd, String licenseType) {
        assertEquals(name, registeredPlayerName());
        ClubRelationship membership = relationships.find(registeredPlayer, createdClubs.idOf(clubName).orElseThrow()).orElseThrow();
        assertEquals(MEMBER, membership.status());
        FfeLicense license = membership.license(new Season(yearBegin, yearEnd)).orElseThrow();
        assertEquals(FfeLicenseType.valueOf(licenseType), license.type());
    }

    @Then("the FFE identifier of {player} is {string}")
    public void ffeIdentifierOfTheRegisteredPlayerIs(PlayerName name, String ffeIdentifier) {
        assertEquals(name, registeredPlayerName());
        assertEquals(Optional.of(new FfeId(ffeIdentifier)), people.find(registeredPlayer).orElseThrow().ffeId());
    }

    private PlayerName registeredPlayerName() {
        Person player = people.find(registeredPlayer).orElseThrow();
        return new PlayerName(player.firstName(), player.lastName());
    }
}
