package clubmanagement.persistence;

import clubmanagement.domain.member.vo.EloRating;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FideTitle;
import clubmanagement.domain.person.Person;
import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.ports.PersonRepository;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class JdbcPersonRepository implements PersonRepository {
    private final JdbcClient jdbc;

    public JdbcPersonRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Person> find(PersonId personId) {
        return jdbc.sql("SELECT id, first_name, last_name, ffe_id, elo, fide_title FROM person WHERE id = :id")
                .param("id", personId.personId())
                .query(JdbcPersonRepository::person)
                .optional();
    }

    @Override
    public void save(Person person) {
        jdbc.sql("""
                        INSERT INTO person (id, first_name, last_name, ffe_id, elo, fide_title)
                        VALUES (:id, :firstName, :lastName, :ffeId, :elo, :fideTitle)""")
                .param("id", person.id().personId())
                .param("firstName", person.firstName())
                .param("lastName", person.lastName())
                .param("ffeId", person.ffeId().map(FfeId::value).orElse(null))
                .param("elo", Optional.ofNullable(person.eloRating()).map(EloRating::rating).orElse(null))
                .param("fideTitle", person.fideTitle().map(FideTitle::name).orElse(null))
                .update();
    }

    private static Person person(ResultSet row, int rowNumber) throws SQLException {
        PersonId id = new PersonId(row.getObject("id", UUID.class));
        String ffeId = row.getString("ffe_id");
        Optional<EloRating> elo = Optional.ofNullable(row.getObject("elo", Integer.class)).map(EloRating::new);
        if (ffeId == null) {
            Person person = new Person(id, row.getString("first_name"), row.getString("last_name"));
            elo.ifPresent(person::giveEloRating);
            return person;
        }
        Optional<FideTitle> title = Optional.ofNullable(row.getString("fide_title")).map(FideTitle::valueOf);
        return Person.registeredLicensedPlayer(id, row.getString("first_name"), row.getString("last_name"),
                new FfeId(ffeId), elo, title);
    }
}
