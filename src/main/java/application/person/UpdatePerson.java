package application.person;

import clubmanagement.domain.member.vo.EloRating;
import clubmanagement.domain.member.vo.FideId;
import clubmanagement.domain.exceptions.FideIdAlreadyAssignedException;
import clubmanagement.domain.person.Person;
import clubmanagement.domain.person.vo.PersonId;

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
