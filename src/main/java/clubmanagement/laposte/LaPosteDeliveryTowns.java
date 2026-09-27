package clubmanagement.laposte;

import clubmanagement.domain.club.vo.DeliveryTown;
import clubmanagement.domain.club.vo.Postcode;
import clubmanagement.domain.commune.CommuneCode;
import clubmanagement.ports.DeliveryTowns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

/** Delivery towns read from La Poste's official postcodes (see resources/laposte/README.md). */
public class LaPosteDeliveryTowns implements DeliveryTowns {
    private static final String OFFICIAL_POSTCODES = "/laposte/laposte_hexasmal.csv";
    private static final int COMMUNE = 0;
    private static final int POSTCODE = 2;
    private static final int DELIVERY_TOWN = 3;

    private final Map<Postcode, List<String>> towns;
    private final Map<CommuneCode, List<DeliveryTown>> townsOfCommunes;

    private LaPosteDeliveryTowns(List<String[]> rows) {
        // A town is listed once per locality (Ligne_5) it serves with the same postcode.
        this.towns = rows.stream().collect(Collectors.groupingBy(row -> new Postcode(row[POSTCODE]),
                Collectors.collectingAndThen(
                        Collectors.mapping(row -> row[DELIVERY_TOWN], Collectors.toCollection(LinkedHashSet::new)),
                        List::copyOf)));
        this.townsOfCommunes = rows.stream().collect(Collectors.groupingBy(row -> new CommuneCode(row[COMMUNE]),
                Collectors.collectingAndThen(
                        Collectors.groupingBy(row -> row[DELIVERY_TOWN], LinkedHashMap::new,
                                Collectors.mapping(row -> new Postcode(row[POSTCODE]), Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Postcode::value))))),
                        LaPosteDeliveryTowns::deliveryTowns)));
    }

    private static List<DeliveryTown> deliveryTowns(Map<String, TreeSet<Postcode>> postcodesOfTowns) {
        return postcodesOfTowns.entrySet().stream()
                .map(town -> new DeliveryTown(town.getKey(), List.copyOf(town.getValue())))
                .toList();
    }

    public static LaPosteDeliveryTowns fromOfficialPostcodes() {
        try (InputStream file = LaPosteDeliveryTowns.class.getResourceAsStream(OFFICIAL_POSTCODES);
             BufferedReader lines = new BufferedReader(new InputStreamReader(file, StandardCharsets.ISO_8859_1))) {
            return new LaPosteDeliveryTowns(lines.lines().skip(1).map(line -> line.split(";", -1)).toList());
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + OFFICIAL_POSTCODES, e);
        }
    }

    @Override
    public List<String> servedBy(Postcode postcode) {
        return towns.getOrDefault(postcode, List.of());
    }

    @Override
    public List<Postcode> postcodesOf(String town) {
        String asLaPosteWritesIt = asLaPosteWritesIt(town);
        return towns.entrySet().stream()
                .filter(served -> served.getValue().contains(asLaPosteWritesIt))
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparing(Postcode::value))
                .toList();
    }

    @Override
    public List<DeliveryTown> ofCommune(CommuneCode commune) {
        return townsOfCommunes.getOrDefault(commune, List.of());
    }

    // La Poste writes a town in capitals, without accents nor punctuation, and abbreviates the words
    // SAINT and SAINTE; a town such as SAINTES keeps its name in full.
    private static String asLaPosteWritesIt(String town) {
        String withoutAccents = Normalizer.normalize(town, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return withoutAccents.replaceAll("[^\\p{L}\\p{N}]", " ")
                .toUpperCase(Locale.ROOT)
                .replaceAll("\\bSAINT(E?)\\b", "ST$1");
    }
}
