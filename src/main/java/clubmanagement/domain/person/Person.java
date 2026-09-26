package clubmanagement.domain.person;

import clubmanagement.domain.exceptions.FideIdAlreadyAssignedException;
import clubmanagement.domain.member.vo.EloRating;
import clubmanagement.domain.member.vo.FideId;
import clubmanagement.domain.person.vo.PersonId;

import java.util.Objects;
import java.util.Optional;

public class Person {

    private final PersonId personId;
    private FideId fideId;
    private final String firstName;
    private final String lastName;
    private final String email;

    private EloRating eloRating;
    private EloRating eloRatingLastRecorded;


    public Person(PersonId personId,
                  String firstName,
                  String lastName) {
        this(personId, firstName, lastName, null);
    }

    public Person(PersonId personId, String firstName, String lastName, String email) {
        if (personId == null) throw new IllegalArgumentException("personId cannot be null");
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }


    public String firstName() {
        return firstName;
    }
    public Optional<String> email() { return Optional.ofNullable(email); }
    public String lastName() {
        return lastName;
    }

    public void giveEloRating(EloRating eloRating) {
        this.eloRating = eloRating;
    }

    public void recordEloRating(EloRating eloRating) {
        this.eloRatingLastRecorded = this.eloRating;
        this.eloRating = eloRating;
    }

    public EloRating eloRating() {
    return this.eloRating;
    }

    public void registerFideId(FideId fideId) throws FideIdAlreadyAssignedException {
        if (this.fideId != null) throw new FideIdAlreadyAssignedException();
        this.fideId = fideId;
    }

    public Optional<FideId> fideId() {return Optional.ofNullable(fideId);}

    public PersonId id() {return personId;}
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(personId, person.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(personId);
    }
}
