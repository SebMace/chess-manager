package application.person;

import domain.person.Person;
import domain.person.vo.PersonId;

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
