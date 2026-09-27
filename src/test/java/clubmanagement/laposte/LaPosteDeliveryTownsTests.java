package clubmanagement.laposte;

import clubmanagement.domain.club.vo.DeliveryTown;
import clubmanagement.domain.club.vo.Postcode;
import clubmanagement.domain.commune.CommuneCode;
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

    @Test
    void should_find_a_town_typed_in_small_letters_and_with_its_accents() {
        assertEquals(List.of(new Postcode("45000"), new Postcode("45100")), deliveryTowns.postcodesOf("Orléans"));
    }

    @Test
    void should_find_a_town_typed_with_hyphens() {
        assertEquals(List.of(new Postcode("45240")), deliveryTowns.postcodesOf("Ligny-le-Ribault"));
    }

    @Test
    void should_find_a_town_typed_with_an_apostrophe() {
        assertEquals(List.of(new Postcode("01400")), deliveryTowns.postcodesOf("L'Abergement-Clémenciat"));
    }

    @Test
    void should_find_a_town_typed_with_saint_in_full() {
        assertEquals(List.of(new Postcode("45800")), deliveryTowns.postcodesOf("Saint-Jean-de-Braye"));
    }

    @Test
    void should_find_a_town_typed_with_sainte_in_full() {
        assertEquals(List.of(new Postcode("45230"), new Postcode("91700")), deliveryTowns.postcodesOf("Sainte-Geneviève-des-Bois"));
    }

    @Test
    void should_not_abbreviate_a_town_that_only_begins_with_saint() {
        assertEquals(List.of(new Postcode("17100")), deliveryTowns.postcodesOf("Saintes"));
    }

    @Test
    void should_offer_the_town_of_a_commune_with_each_of_its_postcodes_once() {
        assertEquals(List.of(new DeliveryTown("ORLEANS", List.of(new Postcode("45000"), new Postcode("45100")))),
                deliveryTowns.ofCommune(new CommuneCode("45234")));
    }

    @Test
    void should_offer_the_town_of_a_commune_without_the_communes_of_the_same_name() {
        assertEquals(List.of(new DeliveryTown("OLIVET", List.of(new Postcode("45160")))),
                deliveryTowns.ofCommune(new CommuneCode("45232")));
    }

    @Test
    void should_offer_the_town_la_poste_delivers_a_commune_as_even_when_its_name_differs() {
        assertEquals(List.of(new DeliveryTown("COLMARS LES ALPES", List.of(new Postcode("04370")))),
                deliveryTowns.ofCommune(new CommuneCode("04061")));
    }
}
