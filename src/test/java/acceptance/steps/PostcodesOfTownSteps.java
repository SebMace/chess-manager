package acceptance.steps;

import clubmanagement.domain.club.vo.Postcode;
import clubmanagement.ports.InMemoryDeliveryTowns;
import clubmanagement.postcodesoftown.PostcodesOfTown;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostcodesOfTownSteps {
    private final InMemoryDeliveryTowns deliveryTowns = new InMemoryDeliveryTowns();
    private List<Postcode> offeredPostcodes = List.of();

    @When("an administrator looks for the postcodes of the town {string}")
    public void lookForPostcodes(String town) {
        offeredPostcodes = new PostcodesOfTown(deliveryTowns).execute(town);
    }

    @Then("the administrator is offered the postcode {string}")
    public void postcodeOffered(String postcode) {
        assertTrue(offeredPostcodes.contains(new Postcode(postcode)),
                () -> postcode + " should be offered among " + offeredPostcodes);
    }

    @Then("the administrator is not offered the postcode {string}")
    public void postcodeNotOffered(String postcode) {
        assertFalse(offeredPostcodes.contains(new Postcode(postcode)), () -> postcode + " should not be offered");
    }
}
