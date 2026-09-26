package clubmanagement.ports;

import clubmanagement.domain.person.Person;
import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.domain.exceptions.FideIdAlreadyAssignedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryPersonRepository implements PersonRepository {
    private final Map<PersonId, Person> people = new HashMap<>();

    public Optional<Person> find(PersonId personId) {
        return Optional.ofNullable(people.get(personId)).map(this::snapshot);
    }

    public void save(Person person) { people.put(person.id(), snapshot(person)); }

    private Person snapshot(Person person) {
        Person copy = new Person(person.id(), person.firstName(), person.lastName(), person.email().orElse(null));
        copy.giveEloRating(person.eloRating());
        try {
            if (person.fideId().isPresent()) copy.registerFideId(person.fideId().orElseThrow());
        } catch (FideIdAlreadyAssignedException exception) {
            throw new AssertionError("A fresh snapshot cannot already have a FIDE identifier", exception);
        }
        return copy;
    }
}
