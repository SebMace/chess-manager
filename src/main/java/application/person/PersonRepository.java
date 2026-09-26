package application.person;

import clubmanagement.domain.person.Person;
import clubmanagement.domain.person.vo.PersonId;
import java.util.Optional;

public interface PersonRepository {
    Optional<Person> find(PersonId personId);
    void save(Person person);
}
