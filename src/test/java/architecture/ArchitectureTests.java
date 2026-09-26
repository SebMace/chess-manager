package architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

class ArchitectureTests {
    private static final String REST_ADAPTERS = "..rest..";
    private static final String PERSISTENCE_ADAPTER = "clubmanagement.persistence..";
    private static final String INSEE_ADAPTER = "clubmanagement.insee..";

    private static final JavaClasses CLUB_MANAGEMENT = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("clubmanagement");

    @Test
    void domain_should_not_depend_on_slices_ports_or_infrastructure() {
        noClasses().that().resideInAPackage("clubmanagement.domain..")
                .should().dependOnClassesThat(
                        resideInAnyPackage("clubmanagement..", "infrastructure..")
                                .and(resideOutsideOfPackage("clubmanagement.domain..")))
                .because("the domain must remain independent of the slices, their ports and the infrastructure")
                .check(CLUB_MANAGEMENT);
    }

    @Test
    void core_should_not_depend_on_frameworks_or_adapters() {
        noClasses().that().resideInAPackage("clubmanagement..")
                .and().resideOutsideOfPackages(REST_ADAPTERS, PERSISTENCE_ADAPTER, INSEE_ADAPTER)
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "infrastructure..",
                        REST_ADAPTERS, PERSISTENCE_ADAPTER, INSEE_ADAPTER)
                .because("the hexagon's core must not know about Spring or about the adapters around it")
                .check(CLUB_MANAGEMENT);
    }

    @Test
    void slices_should_not_depend_on_each_other() {
        slices().matching("clubmanagement.(*)..")
                .should().notDependOnEachOther()
                .ignoreDependency(alwaysTrue(), resideInAnyPackage("clubmanagement.domain..", "clubmanagement.ports.."))
                .because("a slice relies only on the shared domain and ports, never on another slice")
                .check(CLUB_MANAGEMENT);
    }
}
