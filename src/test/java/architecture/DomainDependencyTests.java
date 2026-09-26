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
                .importPackages("clubmanagement.domain");

        noClasses().that().resideInAPackage("clubmanagement.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("clubmanagement.ports..", "application..", "infrastructure..", "infra..", "adapters..")
                .because("the domain must remain independent of application orchestration and infrastructure")
                .check(domainClasses);
    }

    @Test
    void core_should_not_depend_on_frameworks_or_adapters() {
        JavaClasses coreClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("clubmanagement", "application");

        noClasses().that().resideInAnyPackage("clubmanagement..", "application..")
                .and().resideOutsideOfPackage("..rest..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "adapters..", "infrastructure..", "..rest..")
                .because("the hexagon's core must not know about Spring or about the adapters around it")
                .check(coreClasses);
    }
}
