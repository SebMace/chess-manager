package club;

import domain.club.vo.CommitteeCode;
import domain.commune.DepartmentCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommitteeCodeTests {
    @Test
    void should_cover_the_department_with_the_same_code_in_metropolitan_france() {
        assertEquals(new DepartmentCode("45"), new CommitteeCode("45").department());
    }
}
