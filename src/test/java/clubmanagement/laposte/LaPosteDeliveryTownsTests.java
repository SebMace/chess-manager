package clubmanagement.laposte;

import clubmanagement.domain.club.vo.Postcode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LaPosteDeliveryTownsTests {
    private final LaPosteDeliveryTowns deliveryTowns = LaPosteDeliveryTowns.fromOfficialPostcodes();

    @Test
    void should_offer_each_town_once_even_when_la_poste_lists_it_for_several_localities() {
        assertEquals(List.of("ORLEANS"), deliveryTowns.servedBy(new Postcode("45100")));
    }

    @Test
    void should_offer_every_town_a_postcode_serves() {
        assertEquals(
                List.of("LA FERTE ST AUBIN", "LIGNY LE RIBAULT", "MARCILLY EN VILLETTE", "MENESTREAU EN VILLETTE", "SENNELY"),
                deliveryTowns.servedBy(new Postcode("45240")));
    }

    @Test
    void should_offer_no_town_for_a_postcode_la_poste_does_not_know() {
        assertEquals(List.of(), deliveryTowns.servedBy(new Postcode("99999")));
    }

    @Test
    void should_offer_each_postcode_of_a_town_once_even_when_la_poste_lists_it_for_several_localities() {
        assertEquals(List.of(new Postcode("45000"), new Postcode("45100")), deliveryTowns.postcodesOf("ORLEANS"));
    }

    @Test
    void should_offer_the_postcodes_of_every_town_of_the_same_name() {
        assertEquals(List.of(new Postcode("45160"), new Postcode("53410")), deliveryTowns.postcodesOf("OLIVET"));
    }
}
