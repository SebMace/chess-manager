package application.person;

import domain.person.Person;
import domain.person.vo.PersonId;
import java.util.Optional;

public interface PersonRepository {
    Optional<Person> find(PersonId personId);
    void save(Person person);
}
