package clubmanagement.ports;

import clubmanagement.domain.club.vo.Postcode;

import java.util.List;
import java.util.Map;

public class InMemoryDeliveryTowns implements DeliveryTowns {
    private final Map<Postcode, List<String>> towns = Map.of(
            new Postcode("45240"), List.of(
                    "LA FERTE ST AUBIN", "LIGNY LE RIBAULT", "MARCILLY EN VILLETTE", "MENESTREAU EN VILLETTE", "SENNELY"),
            new Postcode("45000"), List.of("ORLEANS"),
            new Postcode("45100"), List.of("ORLEANS"));

    @Override
    public List<String> servedBy(Postcode postcode) {
        return towns.getOrDefault(postcode, List.of());
    }
}
