package person;

import domain.person.Person;
import domain.person.vo.PersonId;
import domain.member.vo.EloRating;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryPersonRepositoryTests {
    @Test
    void should_only_persist_changes_when_explicitly_saved() {
        var repository = new InMemoryPersonRepository();
        var id = new PersonId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        var original = new Person(id, "Camille", "Martin");
        original.giveEloRating(new EloRating(1500));
        repository.save(original);
        original.recordEloRating(new EloRating(1700));
        assertEquals(new EloRating(1500), repository.find(id).orElseThrow().eloRating());

        var loaded = repository.find(id).orElseThrow();
        loaded.recordEloRating(new EloRating(1600));
        assertEquals(new EloRating(1500), repository.find(id).orElseThrow().eloRating());
        repository.save(loaded);
        assertEquals(new EloRating(1600), repository.find(id).orElseThrow().eloRating());
    }
}
