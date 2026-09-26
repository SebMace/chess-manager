package commune;

import application.commune.Communes;
import domain.commune.Commune;
import domain.commune.CommuneCode;
import domain.commune.DepartmentCode;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryCommunes implements Communes {
    public static final Commune ORLEANS = loiret("45234", "Orléans");
    public static final Commune OLIVET = loiret("45232", "Olivet");
    public static final Commune SAINT_PRYVE_SAINT_MESMIN = loiret("45298", "Saint-Pryvé-Saint-Mesmin");
    public static final Commune LOURY = loiret("45188", "Loury");
    public static final Commune MONTARGIS = loiret("45208", "Montargis");
    public static final Commune OLIVET_IN_MAYENNE = new Commune(new CommuneCode("53169"), "Olivet", new DepartmentCode("53"));

    private final Map<CommuneCode, Commune> communes = Stream.of(
                    ORLEANS, OLIVET, SAINT_PRYVE_SAINT_MESMIN, LOURY, MONTARGIS, OLIVET_IN_MAYENNE)
            .collect(Collectors.toUnmodifiableMap(Commune::code, Function.identity()));

    @Override
    public Optional<Commune> find(CommuneCode code) {
        return Optional.ofNullable(communes.get(code));
    }

    private static Commune loiret(String code, String name) {
        return new Commune(new CommuneCode(code), name, new DepartmentCode("45"));
    }

    @Override
    public List<Commune> inDepartment(DepartmentCode department) {
        return List.of();
    }
}
