package acceptance.steps;

import clubmanagement.domain.club.vo.DeliveryTown;
import clubmanagement.domain.club.vo.Postcode;
import clubmanagement.domain.commune.CommuneCode;
import clubmanagement.ports.InMemoryCommunes;
import clubmanagement.ports.InMemoryDeliveryTowns;
import clubmanagement.townsofcommune.TownsOfCommune;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TownsOfCommuneSteps {
    private static final Map<String, CommuneCode> COMMUNES = Map.of(
            "Orléans", InMemoryCommunes.ORLEANS.code(),
            "Olivet", InMemoryCommunes.OLIVET.code());
    private final InMemoryDeliveryTowns deliveryTowns = new InMemoryDeliveryTowns();
    private List<DeliveryTown> offeredTowns = List.of();

    @When("an administrator looks for the towns of the commune {string}")
    public void lookForTowns(String commune) {
        offeredTowns = new TownsOfCommune(deliveryTowns).execute(COMMUNES.get(commune));
    }

    @Then("the administrator is offered the town {string} with the postcodes {string}")
    public void townOfferedWith(String town, String postcodes) {
        List<Postcode> expected = Arrays.stream(postcodes.split(",\\s*")).map(Postcode::new).toList();
        assertEquals(List.of(new DeliveryTown(town, expected)), offeredTowns);
    }
}
