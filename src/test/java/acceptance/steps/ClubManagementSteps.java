package acceptance.steps;

import acceptance.support.ClubManagementDriver;
import domain.club.RelationshipStatus;
import domain.club.vo.Season;
import domain.member.vo.FfeLicense;
import domain.person.vo.PersonId;
import java.util.Optional;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ClubManagementSteps {
    private final ClubManagementDriver application = new ClubManagementDriver();
    private String administratorClub;
    private RuntimeException rejection;
    private String suppliedFfeId = "A12345";
    private String suppliedLicenseType = "A";
    private Optional<String> queriedAffiliation;
    private boolean externalPlayer;
    private PersonId originalIdentity;
    private Set<String> requestedPartners = Set.of();

    @Given("the administrator acts on behalf of Orléans")
    @Given("Orléans is a club managed by the application")
    public void administratorActsOnBehalfOfOrleans() { application.defineClub("Orléans", true); administratorClub = "Orléans"; }

    @Given("Camille Martin has no FFE license")
    public void camilleMartinHasNoFfeLicense() {
        assertFalse(application.isAffiliated("Orléans"));
    }

    @When("the administrator registers prospect {string} {string} with email {string}")
    public void registerProspect(String firstName, String lastName, String email) {
        application.registerProspect(administratorClub, firstName, lastName, email);
    }

    @Then("prospect {string} {string} is recorded for Orléans")
    public void prospectIsRecordedForOrleans(String firstName, String lastName) {
        assertEquals(firstName, application.person().firstName());
        assertEquals(lastName, application.person().lastName());
        assertTrue(application.hasStatus("Orléans", RelationshipStatus.PROSPECT));
    }

    @Then("the recorded prospect's email is {string}")
    public void recordedProspectEmailIs(String email) {
        assertEquals(email, application.person().email().orElseThrow());
    }

    @Given("Camille has an FFE identifier and an A license at Orléans for the current season")
    public void camilleHasAnALicense() {
        application.registerProspect("Orléans", "Camille", "Martin", "camille@example.org");
        application.registerLicense("Orléans", "A", Set.of());
    }

    @When("an attempt is made to record Camille as a prospect of Orléans")
    public void attemptToRecordProspect() {
        rejection = assertThrows(IllegalStateException.class,
                () -> application.registerExistingPersonAsProspect("Orléans"));
    }

    @Then("prospect registration is rejected")
    public void prospectRegistrationIsRejected() {
        assertNotNull(rejection);
        assertTrue(application.hasStatus("Orléans", RelationshipStatus.MEMBER));
        assertFalse(application.hasStatus("Orléans", RelationshipStatus.PROSPECT));
    }
    @Given("Camille is a prospect of Orléans and Olivet")
    public void prospectOfBothClubs() {
        application.registerProspect("Orléans", "Camille", "Martin", "camille@example.org");
        application.registerExistingPersonAsProspect("Olivet");
    }

    @Given("Camille explicitly wishes to remain a partner of Olivet")
    public void wishesToRemainPartner() { requestedPartners = Set.of("Olivet"); }

    @When("Camille registers an FFE license of type {string} at Orléans for the current season")
    public void registerLicense(String type) { application.registerLicense("Orléans", type, requestedPartners); }

    @Then("Camille is a member of Orléans for the current season")
    public void memberOfOrleans() {
        assertTrue(application.hasStatus("Orléans", RelationshipStatus.MEMBER));
        assertTrue(application.isAffiliated("Orléans"));
    }

    @Then("Camille is no longer a prospect of Orléans")
    public void noLongerProspectOfOrleans() {
        assertFalse(application.hasStatus("Orléans", RelationshipStatus.PROSPECT));
    }

    @Then("Camille has no relationship with Olivet")
    public void noRelationshipWithOlivet() { assertFalse(application.hasRelationship("Olivet")); }

    @Then("Camille is a partner of Olivet")
    public void partnerOfOlivet() { assertTrue(application.hasStatus("Olivet", RelationshipStatus.PARTNER)); }

    @Then("Camille is no longer a prospect of Olivet")
    public void noLongerProspectOfOlivet() { assertFalse(application.hasStatus("Olivet", RelationshipStatus.PROSPECT)); }

    @Then("Camille is not affiliated with Olivet for the current season")
    public void notAffiliatedWithOlivet() { assertFalse(application.isAffiliated("Olivet")); }

    @Given("Camille has neither an A license nor a B license")
    public void withoutLicense() {
        application.registerProspect("Orléans", "Camille", "Martin", "camille@example.org");
        suppliedLicenseType = null;
    }

    @Given("an A license is supplied for Camille without an FFE identifier")
    public void withoutFfeId() {
        application.registerProspect("Orléans", "Camille", "Martin", "camille@example.org");
        suppliedFfeId = null;
    }

    @When("an attempt is made to create Camille as a member")
    public void attemptMember() {
        if (suppliedLicenseType == null) {
            rejection = assertThrows(IllegalArgumentException.class,
                    () -> application.registerLicense("Orléans", null, Set.of()));
        } else {
            attempt(() -> application.registerLicenseDetails("Orléans", suppliedFfeId, suppliedLicenseType, application.currentSeason()));
        }
    }

    @Then("member creation is rejected")
    @Then("the request is rejected")
    @Then("the FFE identifier is rejected")
    public void requestRejected() { assertNotNull(rejection); }

    @Then("no member is created")
    public void noMember() {
        assertFalse(application.hasStatus("Orléans", RelationshipStatus.MEMBER));
        assertFalse(application.isAffiliated("Orléans"));
    }

    @Given("Camille's FFE identifier is {string}")
    public void suppliedFfe(String identifier) { application.ensurePerson(); suppliedFfeId = identifier; }

    @When("Camille's FFE registration is recorded with category {word}")
    public void recordFfe(String type) {
        application.registerLicenseDetails("Orléans", suppliedFfeId, type, application.currentSeason());
    }

    @Then("the recorded FFE identifier is {string}")
    @Then("Camille's FFE identifier remains {string}")
    public void recordedFfe(String expected) {
        assertEquals(expected, application.license("Orléans").orElseThrow().ffeId().value());
    }

    @Then("the recorded license category is {word}")
    @Then("Camille's license category remains {word}")
    public void recordedCategory(String expected) {
        assertEquals(expected, application.license("Orléans").orElseThrow().type().name());
    }

    @Given("no FFE registration is recorded for Camille")
    public void noFfeRegistration() { application.ensurePerson(); assertFalse(application.hasAnyAffiliation()); }

    @When("registration is requested without the {string}")
    @When("a new registration is requested without the {string}")
    public void incompleteFfe(String missing) {
        String id = missing.equals("FFE identifier") ? null : suppliedFfeId;
        String type = missing.equals("FFE license category") ? null : suppliedLicenseType;
        attempt(() -> application.registerLicenseDetails("Orléans", id, type, application.currentSeason()));
    }

    @Then("no partial FFE registration is recorded")
    public void noPartialRegistration() { assertTrue(application.license("Orléans").isEmpty()); }

    @When("an FFE identifier is created with {string}")
    public void invalidFfe(String description) {
        String value = switch (description) {
            case "no value" -> null;
            case "an empty string" -> "";
            case "only spaces" -> "   ";
            case "only tabs" -> "\t";
            case "only line breaks" -> "\n";
            default -> throw new IllegalArgumentException("Unknown example: " + description);
        };
        attempt(() -> application.registerLicenseDetails("Orléans", value, "A", application.currentSeason()));
    }

    @When("the FFE identifier {string} is recorded")
    public void recordIdentifier(String value) {
        application.registerLicenseDetails("Orléans", value, "A", application.currentSeason());
    }

    @Then("its value remains exactly {string}")
    public void unchangedIdentifier(String value) { recordedFfe(value); }

    @Given("Camille has recorded FFE identifier {string} and category A")
    public void initialFfe(String value) { suppliedFfeId = value; recordIdentifier(value); }

    @Given("Camille has supplied an FFE identifier and an A license")
    public void suppliedLicense() { application.ensurePerson(); suppliedFfeId = "A12345"; suppliedLicenseType = "A"; }

    @Given("Camille has an FFE identifier and a(n) {word} license")
    @Given("Camille has an FFE identifier and a license of type {word}")
    public void suppliedType(String type) { application.ensurePerson(); suppliedLicenseType = type; }

    @Given("Camille has no affiliation for the {int}-{int} season")
    @Given("Camille is not yet affiliated for the {int}-{int} season")
    public void noAffiliation(int begin, int end) {
        application.currentSeason(new Season(begin, end));
        application.ensurePerson();
        originalIdentity = application.person().id();
        assertTrue(application.affiliation(application.currentSeason()).isEmpty());
    }

    @Given("Camille is affiliated with {word} for the {int}-{int} season")
    public void initialAffiliation(String club, int begin, int end) {
        application.currentSeason(new Season(begin, end));
        affiliate(club, begin, end);
    }

    @When("Camille is affiliated with {word} for that season")
    public void affiliateCurrent(String club) {
        application.registerLicenseDetails(club, suppliedFfeId, suppliedLicenseType, application.currentSeason());
    }

    public void affiliate(String club, int begin, int end) {
        application.registerLicenseDetails(club, suppliedFfeId, suppliedLicenseType, new Season(begin, end));
    }

    @Then("Camille's club for the {int}-{int} season is {string}")
    public void clubForSeason(int begin, int end, String club) {
        assertEquals(Optional.of(club), application.affiliation(new Season(begin, end)));
    }

    @When("Camille's club for the {int}-{int} season is requested")
    public void queryAffiliation(int begin, int end) { queriedAffiliation = application.affiliation(new Season(begin, end)); }

    @Then("no affiliation is returned for that season")
    public void noQueriedAffiliation() { assertEquals(Optional.empty(), queriedAffiliation); }

    @When("that affiliation is requested again")
    public void repeatAffiliation() { affiliateCurrent("Orléans"); }

    @Then("the request succeeds without changing the affiliation")
    public void unchangedAffiliation() {
        assertEquals(Optional.of("Orléans"), application.affiliation(application.currentSeason()));
        recordedFfe(suppliedFfeId);
        recordedCategory(suppliedLicenseType);
    }

    @When("an affiliation with Olivet is requested for the same season")
    public void secondAffiliation() { attempt(() -> affiliateCurrent("Olivet")); }

    @Then("Camille's club for that season remains Orléans")
    public void originalClubRemains() {
        assertEquals(Optional.of("Orléans"), application.affiliation(application.currentSeason()));
        assertFalse(application.isAffiliated("Olivet"));
    }

    @Given("Camille has no recorded affiliations")
    public void noAffiliations() { application.ensurePerson(); assertFalse(application.hasAnyAffiliation()); }

    @When("an affiliation is requested without a {word}")
    public void incompleteAffiliation(String missing) {
        attempt(() -> application.registerLicenseDetails(missing.equals("club") ? null : "Orléans",
                suppliedFfeId, suppliedLicenseType, missing.equals("season") ? null : application.currentSeason()));
    }

    @Then("Camille still has no recorded affiliations")
    public void stillNoAffiliations() { assertFalse(application.hasAnyAffiliation()); }

    @When("Camille is registered as a member of Orléans for that season")
    public void registerMember() { affiliateCurrent("Orléans"); }

    @Then("Camille is a member of Orléans for that season")
    public void memberForSeason() { memberOfOrleans(); }

    @Then("Camille retains the same internal identity and FFE identifier")
    public void identifiersRetained() { assertEquals(originalIdentity, application.person().id()); recordedFfe(suppliedFfeId); }

    @Given("Orléans and Olivet are clubs managed by the application")
    public void managedClubs() { application.defineClub("Orléans", true); application.defineClub("Olivet", true); }

    @Given("Gien is a club not managed by the application")
    public void externalClub() { application.defineClub("Gien", false); }

    @Given("Camille is a member of Olivet for the {int}-{int} season")
    public void memberOfOlivet(int begin, int end) {
        initialAffiliation("Olivet", begin, end);
        originalIdentity = application.person().id();
    }

    @When("a partnership between Camille and {word} is recorded")
    public void partnership(String club) {
        originalIdentity = application.person().id();
        application.registerPartnership(club);
    }

    @Then("Camille is a partner of Orléans")
    public void partnerOfOrleans() { assertTrue(application.hasStatus("Orléans", RelationshipStatus.PARTNER)); }

    @Then("Camille remains a member of Olivet for that season")
    public void stillMemberOfOlivet() {
        assertTrue(application.hasStatus("Olivet", RelationshipStatus.MEMBER));
        assertEquals(Optional.of("Olivet"), application.affiliation(application.currentSeason()));
    }

    @Then("both relationships refer to the same person")
    public void samePersonInBothClubs() {
        assertEquals(originalIdentity, application.relationshipPerson("Orléans"));
        assertEquals(originalIdentity, application.relationshipPerson("Olivet"));
    }

    @When("Camille's club profile is consulted")
    public void consultProfile() { externalPlayer = application.isExternalPlayer(); }

    @Then("Camille is represented as an external Player")
    public void externalPlayer() { assertTrue(externalPlayer); }

    @Then("Camille's affiliation with Gien is retained")
    @Then("Camille remains affiliated with Gien")
    @Then("Camille remains affiliated with Gien for that season")
    public void retainsGien() {
        assertEquals(Optional.of("Gien"), application.affiliation(application.currentSeason()));
        assertEquals(suppliedFfeId, application.license("Gien").orElseThrow().ffeId().value());
    }

    @Then("Gien does not become a club managed by the application")
    public void gienStillExternal() { assertFalse(application.isClubManaged("Gien")); }

    @Given("Camille is an external Player affiliated with Gien for the {int}-{int} season")
    public void affiliatedWithExternalClub(int begin, int end) {
        externalClub();
        initialAffiliation("Gien", begin, end);
        originalIdentity = application.person().id();
        assertTrue(application.isExternalPlayer());
    }

    @Then("Camille is not a member of Orléans for that season")
    public void notMemberOfOrleans() {
        assertFalse(application.hasStatus("Orléans", RelationshipStatus.MEMBER));
        assertFalse(application.isAffiliated("Orléans"));
    }

    @Then("Camille remains an external Player affiliated with Gien")
    public void stillExternalPlayer() { assertTrue(application.isExternalPlayer()); retainsGien(); }

    @Then("the partnership refers to Camille's existing identity")
    public void partnershipIdentity() { assertEquals(originalIdentity, application.relationshipPerson("Orléans")); }

    @Given("Camille has requested a partnership with Orléans")
    public void initialPartnership() { application.registerPartnership("Orléans"); }

    @Then("Camille is a partner of Orléans and Olivet")
    public void bothPartnerships() { partnerOfOrleans(); partnerOfOlivet(); }

    @Then("both partnerships refer to the same person")
    public void bothPartnershipIdentities() { samePersonInBothClubs(); }

    private void attempt(Runnable action) {
        rejection = null;
        try { action.run(); }
        catch (IllegalArgumentException | IllegalStateException exception) { rejection = exception; }
    }
}
