package person;

import clubmanagement.domain.exceptions.FideIdAlreadyAssignedException;
import clubmanagement.domain.person.Person;
import clubmanagement.domain.member.vo.EloRating;
import clubmanagement.domain.member.vo.FideId;
import clubmanagement.domain.person.vo.PersonId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PersonTests {

    // Day 1 10/07/2026
    @Test
    void should_create_a_person_with_a_first_name_and_a_last_name() {
        Person person = new Person(
                new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001")),
                "Magnus",
                "Carlsen"
        );
        assertEquals("Magnus", person.firstName());
        assertEquals("Carlsen", person.lastName());
    }
    // Day 2 11/07/2026
    @Test
    void should_reject_a_negative_elo_rating() {
        assertThrows(IllegalArgumentException.class, () -> new EloRating(-1));
    }

    // Day 3 12/07/2026
    // Personal data can evolve while identity stays unchanged.
    // Person's identity never changes

    @Test
    void should_change_the_elo_rating_of_a_person() {
        //Given : one existing person with an initial elo rating.
        Person person = new Person(new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001")),"Sébastien", "Macé");
        person.giveEloRating(new EloRating(1500));
        // when : his rating is updated
        person.recordEloRating(new EloRating(1600));
        // then : tha person gets the new rating
        assertEquals(new EloRating(1600),person.eloRating());
    }

    // Day 4 13/07/2026

    @Test
    void should_reject_replacing_an_existing_fide_id() throws FideIdAlreadyAssignedException {
    //Given  : a person with an existing FIDE ID
        Person person = new Person(new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001")),"Sébastien", "Macé");
        person.registerFideId(new FideId(641839L));
    // When / Then  : assigning another FIDE ID is rejected
        assertThrows(FideIdAlreadyAssignedException.class, () -> person.registerFideId(new FideId(1503014L)));
    }
    // Day 5 14/07/2026
    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    void should_reject_a_non_positive_fide_id(long invalidFideId)  {
        assertThrows(IllegalArgumentException.class, () -> new FideId(invalidFideId));
    }
    // Day 6
    @Test
    void should_create_a_person_with_an_internal_id_and_without_a_fide_id() {
        // Given  : a PersonId, first name, last_name, no FideId
        PersonId personId = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Person person = new Person(personId, "Anatoly", "Karpov");
        assertEquals(personId, person.id());
        assertTrue(person.fideId().isEmpty());
    }
    // Day 7
    @Test
    void should_reject_a_person_without_a_person_id() {
        assertThrows(IllegalArgumentException.class, () -> new Person(null, "Anatoly", "Karpov"));
    }
    // Day 8
    @Test
    void should_reject_a_null_uuid_in_person_id() {
        assertThrows(IllegalArgumentException.class, () -> new PersonId(null));
    }
    // Day 9

    @Test
    void should_consider_persons_with_the_same_person_id_as_the_same_person() {
        // Given: two persons with equal identifiers and different names
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        Person person1 = new Person(new PersonId(uuid), "Anatoly", "Karpov");
        Person person2 = new Person(new PersonId(uuid), "Magnus", "Carlsen");
        // When / Then: they represent the same person
        assertEquals(person1, person2);
    }

    @Test
    void should_consider_homonyms_as_different_persons_if_their_identities_are_different () {
        PersonId personId1 = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        PersonId personId2 = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        Person person1 = new Person(personId1, "Anatoly", "Karpov");
        Person person2 = new Person(personId2, "Anatoly", "Karpov");
        assertNotEquals(person1, person2);
    }

}
