package clubmanagement.laposte;

import clubmanagement.domain.club.vo.Postcode;
import clubmanagement.ports.DeliveryTowns;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Delivery towns read from La Poste's official postcodes (see resources/laposte/README.md). */
public class LaPosteDeliveryTowns implements DeliveryTowns {
    private static final String OFFICIAL_POSTCODES = "/laposte/laposte_hexasmal.csv";
    private static final int POSTCODE = 2;
    private static final int DELIVERY_TOWN = 3;

    private final Map<Postcode, List<String>> towns;

    private LaPosteDeliveryTowns(Map<Postcode, List<String>> towns) {
        this.towns = towns;
    }

    public static LaPosteDeliveryTowns fromOfficialPostcodes() {
        try (InputStream file = LaPosteDeliveryTowns.class.getResourceAsStream(OFFICIAL_POSTCODES);
             BufferedReader lines = new BufferedReader(new InputStreamReader(file, StandardCharsets.ISO_8859_1))) {
            return new LaPosteDeliveryTowns(lines.lines()
                    .skip(1)
                    .map(line -> line.split(";", -1))
                    // A town is listed once per locality (Ligne_5) it serves with the same postcode.
                    .collect(Collectors.groupingBy(row -> new Postcode(row[POSTCODE]),
                            Collectors.collectingAndThen(
                                    Collectors.mapping(row -> row[DELIVERY_TOWN], Collectors.toCollection(LinkedHashSet::new)),
                                    List::copyOf))));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + OFFICIAL_POSTCODES, e);
        }
    }

    @Override
    public List<String> servedBy(Postcode postcode) {
        return towns.getOrDefault(postcode, List.of());
    }
}
