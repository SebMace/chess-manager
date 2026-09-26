package adapters.out.insee;

import domain.commune.Commune;
import domain.commune.CommuneCode;
import domain.commune.DepartmentCode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InseeCommunesTests {
    private final InseeCommunes communes = InseeCommunes.fromOfficialGeographicCode();

    @Test
    void should_find_a_commune_by_its_insee_code() {
        assertEquals(Optional.of(new Commune(new CommuneCode("45234"), "Orléans", new DepartmentCode("45"))),
                communes.find(new CommuneCode("45234")));
    }

    @Test
    void should_not_mistake_a_delegated_commune_for_the_commune_sharing_its_code() {
        assertEquals(Optional.of(new Commune(new CommuneCode("01015"), "Arboys en Bugey", new DepartmentCode("01"))),
                communes.find(new CommuneCode("01015")));
    }

    @Test
    void should_find_nothing_for_an_unknown_code() {
        assertEquals(Optional.empty(), communes.find(new CommuneCode("99999")));
    }

    @Test
    void should_list_the_communes_of_a_department() {
        List<Commune> loiret = communes.inDepartment(new DepartmentCode("45"));

        assertEquals(325, loiret.size());
        assertTrue(loiret.contains(new Commune(new CommuneCode("45232"), "Olivet", new DepartmentCode("45"))));
        assertTrue(loiret.stream().allMatch(commune -> commune.isIn(new DepartmentCode("45"))));
    }
}
