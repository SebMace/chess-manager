package clubmanagement.recordperson;

import clubmanagement.ports.PersonRepository;
import clubmanagement.domain.person.Person;
import clubmanagement.domain.person.vo.PersonId;

import java.util.UUID;

public class RecordPerson {
    private final PersonRepository people;

    public RecordPerson(PersonRepository people) { this.people = people; }

    public PersonId execute(UUID identity, String firstName, String lastName) {
        Person person = new Person(new PersonId(identity), firstName, lastName);
        people.save(person);
        return person.id();
    }
}
