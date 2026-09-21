package architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class DomainDependencyTests {

    @Test
    void domain_should_not_depend_on_application_or_infrastructure() {
        JavaClasses domainClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("domain");

        noClasses().that().resideInAPackage("domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("application..", "infrastructure..", "infra..", "adapters..")
                .because("the domain must remain independent of application orchestration and infrastructure")
                .check(domainClasses);
    }
}
