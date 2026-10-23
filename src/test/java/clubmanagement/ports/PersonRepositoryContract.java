package clubmanagement.ports;

import clubmanagement.domain.member.vo.EloRating;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FideTitle;
import clubmanagement.domain.person.Person;
import clubmanagement.domain.person.vo.PersonId;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** What every PersonRepository promises; each implementation runs these tests against itself. */
public abstract class PersonRepositoryContract {

    protected abstract PersonRepository people();

    @Test
    void should_find_a_licensed_player_that_was_saved() {
        PersonRepository people = people();
        PersonId id = new PersonId(new UUID(0, 1));
        people.save(Person.licensedPlayer(id, "Camille", "Martin", new FfeId("K58213")));

        Person found = people.find(id).orElseThrow();

        assertEquals(id, found.id());
        assertEquals("Camille", found.firstName());
        assertEquals("Martin", found.lastName());
        assertEquals(Optional.of(new FfeId("K58213")), found.ffeId());
        assertNull(found.eloRating());
    }

    @Test
    void should_find_the_rating_of_a_rated_licensed_player_that_was_saved() {
        PersonRepository people = people();
        PersonId id = new PersonId(new UUID(0, 2));
        people.save(Person.registeredLicensedPlayer(id, "Alex", "Durand", new FfeId("K61477"),
                Optional.of(new EloRating(1850)), Optional.empty()));

        Person found = people.find(id).orElseThrow();

        assertEquals(new EloRating(1850), found.eloRating());
        assertEquals(Optional.empty(), found.fideTitle());
    }

    @Test
    void should_find_the_fide_title_of_a_titled_licensed_player_that_was_saved() {
        PersonRepository people = people();
        PersonId id = new PersonId(new UUID(0, 3));
        people.save(Person.registeredLicensedPlayer(id, "Maxime", "Roux", new FfeId("K70001"),
                Optional.of(new EloRating(2310)), Optional.of(FideTitle.MF)));

        Person found = people.find(id).orElseThrow();

        assertEquals(Optional.of(FideTitle.MF), found.fideTitle());
    }
}
