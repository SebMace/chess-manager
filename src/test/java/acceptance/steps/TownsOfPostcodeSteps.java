package acceptance.steps;

import clubmanagement.domain.club.vo.Postcode;
import clubmanagement.ports.InMemoryDeliveryTowns;
import clubmanagement.townsofpostcode.TownsOfPostcode;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TownsOfPostcodeSteps {
    private final InMemoryDeliveryTowns deliveryTowns = new InMemoryDeliveryTowns();
    private List<String> offeredTowns = List.of();

    @When("an administrator looks for the towns of the postcode {string}")
    public void lookForTowns(String postcode) {
        offeredTowns = new TownsOfPostcode(deliveryTowns).execute(new Postcode(postcode));
    }

    @Then("the administrator is offered the town {string}")
    public void townOffered(String town) {
        assertTrue(offeredTowns.contains(town), () -> town + " should be offered among " + offeredTowns);
    }

    @Then("the administrator is not offered the town {string}")
    public void townNotOffered(String town) {
        assertFalse(offeredTowns.contains(town), () -> town + " should not be offered");
    }
}
