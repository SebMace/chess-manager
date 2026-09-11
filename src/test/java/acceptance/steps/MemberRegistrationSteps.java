package acceptance.steps;

import domain.member.entities.Member;
import domain.member.vo.MemberId;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MemberRegistrationSteps {
    private MemberId memberId;
    private Member createdMember;
    private IllegalArgumentException rejection;

    @Given("Camille has neither an A license nor a B license")
    public void camilleHasNoFfeLicense() {
        memberId = new MemberId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    @When("an attempt is made to create Camille as a member")
    public void attemptToCreateMember() {
        try {
            createdMember = new Member(memberId, "Camille", "Martin", null);
        } catch (IllegalArgumentException exception) {
            rejection = exception;
        }
    }

    @Then("member creation is rejected")
    public void memberCreationIsRejected() {
        assertNotNull(rejection, "Member creation must reject a person without an FFE license");
    }

    @Then("no member is created")
    public void noMemberIsCreated() {
        assertNull(createdMember, "A rejected request must not create a member");
    }
}
