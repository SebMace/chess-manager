package application.commune;

import domain.commune.Commune;
import domain.commune.CommuneCode;

import java.util.Optional;

/** Port: the French communes known to Chess Manager. */
public interface Communes {
    Optional<Commune> find(CommuneCode code);
}
