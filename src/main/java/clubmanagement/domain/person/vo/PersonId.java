package clubmanagement.domain.person.vo;

import java.io.Serializable;
import java.util.UUID;

public record PersonId(UUID personId) implements Serializable {

    public PersonId(UUID personId) {
        if (personId == null) throw new IllegalArgumentException("personId cannot be null");
        this.personId = personId;
    }
}
