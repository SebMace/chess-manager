package application;

import clubmanagement.recordperson.RecordPerson;
import clubmanagement.updateperson.UpdatePerson;
import clubmanagement.domain.member.vo.EloRating;
import clubmanagement.domain.member.vo.FideId;
import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.domain.exceptions.FideIdAlreadyAssignedException;
import org.junit.jupiter.api.Test;
import person.InMemoryPersonRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RecordPersonTests {
    private final UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final InMemoryPersonRepository people = new InMemoryPersonRepository();

    @Test
    void should_save_personal_identity_without_requiring_a_license() {
        PersonId id = new RecordPerson(people).execute(uuid, "Camille", "Martin");

        var saved = people.find(id).orElseThrow();
        assertEquals(new PersonId(uuid), saved.id());
        assertEquals("Camille", saved.firstName());
        assertEquals("Martin", saved.lastName());
        assertTrue(saved.fideId().isEmpty());
    }
    @Test
    void should_save_a_rating_update() {
        PersonId id = new RecordPerson(people).execute(uuid, "Camille", "Martin");
        UpdatePerson update = new UpdatePerson(people);
        update.recordEloRating(id, 1500);
        update.recordEloRating(id, 1600);
        assertEquals(new EloRating(1600), people.find(id).orElseThrow().eloRating());
        assertThrows(IllegalArgumentException.class, () -> update.recordEloRating(id, -1));
        assertEquals(new EloRating(1600), people.find(id).orElseThrow().eloRating());
    }
    @Test
    void should_save_the_fide_identifier_and_preserve_it_after_a_rejected_replacement() throws Exception {
        PersonId id = new RecordPerson(people).execute(uuid, "Camille", "Martin");
        UpdatePerson update = new UpdatePerson(people);
        update.registerFideId(id, 641839L);
        assertEquals(new FideId(641839L), people.find(id).orElseThrow().fideId().orElseThrow());
        assertThrows(FideIdAlreadyAssignedException.class, () -> update.registerFideId(id, 1503014L));
        assertEquals(new FideId(641839L), people.find(id).orElseThrow().fideId().orElseThrow());
    }
}
