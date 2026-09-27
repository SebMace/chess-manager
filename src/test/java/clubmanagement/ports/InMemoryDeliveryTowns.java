package clubmanagement.ports;

import clubmanagement.domain.club.vo.DeliveryTown;
import clubmanagement.domain.club.vo.Postcode;
import clubmanagement.domain.commune.CommuneCode;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class InMemoryDeliveryTowns implements DeliveryTowns {
    private final Map<Postcode, List<String>> towns = Map.of(
            new Postcode("45240"), List.of(
                    "LA FERTE ST AUBIN", "LIGNY LE RIBAULT", "MARCILLY EN VILLETTE", "MENESTREAU EN VILLETTE", "SENNELY"),
            new Postcode("45000"), List.of("ORLEANS"),
            new Postcode("45100"), List.of("ORLEANS"));

    private final Map<CommuneCode, List<DeliveryTown>> townsOfCommunes = Map.of(
            InMemoryCommunes.ORLEANS.code(), List.of(new DeliveryTown("ORLEANS", List.of(new Postcode("45000"), new Postcode("45100")))),
            InMemoryCommunes.OLIVET.code(), List.of(new DeliveryTown("OLIVET", List.of(new Postcode("45160")))),
            InMemoryCommunes.OLIVET_IN_MAYENNE.code(), List.of(new DeliveryTown("OLIVET", List.of(new Postcode("53410")))));

    @Override
    public List<String> servedBy(Postcode postcode) {
        return towns.getOrDefault(postcode, List.of());
    }

    @Override
    public List<Postcode> postcodesOf(String town) {
        return towns.entrySet().stream()
                .filter(served -> served.getValue().contains(town))
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparing(Postcode::value))
                .toList();
    }

    @Override
    public List<DeliveryTown> ofCommune(CommuneCode commune) {
        return townsOfCommunes.getOrDefault(commune, List.of());
    }
}
