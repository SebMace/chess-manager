package adapters.out.insee;

import application.commune.Communes;
import domain.commune.Commune;
import domain.commune.CommuneCode;

import java.util.Optional;

public class InseeCommunes implements Communes {
    public static InseeCommunes fromOfficialGeographicCode() {
        return new InseeCommunes();
    }

    @Override
    public Optional<Commune> find(CommuneCode code) {
        return Optional.empty();
    }
}
