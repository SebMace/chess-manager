package adapters.out.insee;

import application.commune.Communes;
import domain.commune.Commune;
import domain.commune.CommuneCode;
import domain.commune.DepartmentCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Communes read from the INSEE official geographic code (see resources/insee/README.md). */
public class InseeCommunes implements Communes {
    private static final String OFFICIAL_GEOGRAPHIC_CODE = "/insee/v_commune_2026.csv";
    private static final String COMMUNE_TYPE = "COM";

    private final Map<CommuneCode, Commune> communes;

    private InseeCommunes(Map<CommuneCode, Commune> communes) {
        this.communes = communes;
    }

    public static InseeCommunes fromOfficialGeographicCode() {
        try (InputStream file = InseeCommunes.class.getResourceAsStream(OFFICIAL_GEOGRAPHIC_CODE);
             BufferedReader lines = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8))) {
            return new InseeCommunes(lines.lines()
                    .skip(1)
                    .map(InseeCommunes::columns)
                    .filter(row -> row[0].equals(COMMUNE_TYPE))
                    .map(row -> new Commune(new CommuneCode(row[1]), row[9], new DepartmentCode(row[3])))
                    .collect(Collectors.toUnmodifiableMap(Commune::code, Function.identity())));
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + OFFICIAL_GEOGRAPHIC_CODE, e);
        }
    }

    // Every field of the file is quoted and none contains a comma or a quote.
    private static String[] columns(String line) {
        return line.substring(1, line.length() - 1).split("\",\"", -1);
    }

    @Override
    public Optional<Commune> find(CommuneCode code) {
        return Optional.ofNullable(communes.get(code));
    }

    @Override
    public List<Commune> inDepartment(DepartmentCode department) {
        return communes.values().stream().filter(commune -> commune.isIn(department)).toList();
    }
}
