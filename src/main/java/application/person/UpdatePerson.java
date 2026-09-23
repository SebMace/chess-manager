package application.person;

import domain.member.vo.EloRating;
import domain.member.vo.FideId;
import domain.exceptions.FideIdAlreadyAssignedException;
import domain.person.Person;
import domain.person.vo.PersonId;

public class UpdatePerson {
    private final PersonRepository people;

    public UpdatePerson(PersonRepository people) { this.people = people; }

    public void recordEloRating(PersonId personId, int rating) {
        Person person = people.find(personId).orElseThrow();
        person.recordEloRating(new EloRating(rating));
        people.save(person);
    }

    public void registerFideId(PersonId personId, long fideId) throws FideIdAlreadyAssignedException {
        Person person = people.find(personId).orElseThrow();
        person.registerFideId(new FideId(fideId));
        people.save(person);
    }
}
