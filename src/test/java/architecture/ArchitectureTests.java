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
    private static final String LA_POSTE_ADAPTER = "clubmanagement.laposte..";
    private static final String COMPETITION_PERSISTENCE_ADAPTER = "competition.persistence..";

    private static final JavaClasses CLUB_MANAGEMENT = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("clubmanagement");

    private static final JavaClasses COMPETITION = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("competition");

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
    void competition_domain_should_not_depend_on_slices_ports_or_infrastructure() {
        noClasses().that().resideInAPackage("competition.domain..")
                .should().dependOnClassesThat(
                        resideInAnyPackage("competition..", "infrastructure..")
                                .and(resideOutsideOfPackage("competition.domain..")))
                .because("the domain must remain independent of the slices, their ports and the infrastructure")
                .check(COMPETITION);
    }

    @Test
    void core_should_not_depend_on_frameworks_or_adapters() {
        noClasses().that().resideInAPackage("clubmanagement..")
                .and().resideOutsideOfPackages(REST_ADAPTERS, PERSISTENCE_ADAPTER, INSEE_ADAPTER, LA_POSTE_ADAPTER)
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "infrastructure..",
                        REST_ADAPTERS, PERSISTENCE_ADAPTER, INSEE_ADAPTER, LA_POSTE_ADAPTER)
                .because("the hexagon's core must not know about Spring or about the adapters around it")
                .check(CLUB_MANAGEMENT);
    }

    @Test
    void competition_core_should_not_depend_on_frameworks_or_adapters() {
        noClasses().that().resideInAPackage("competition..")
                .and().resideOutsideOfPackages(REST_ADAPTERS, COMPETITION_PERSISTENCE_ADAPTER)
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "infrastructure..",
                        REST_ADAPTERS, COMPETITION_PERSISTENCE_ADAPTER)
                .because("the hexagon's core must not know about Spring or about the adapters around it")
                .check(COMPETITION);
    }

    @Test
    void slices_should_not_depend_on_each_other() {
        slices().matching("clubmanagement.(*)..")
                .should().notDependOnEachOther()
                .ignoreDependency(alwaysTrue(), resideInAnyPackage("clubmanagement.domain..", "clubmanagement.ports.."))
                .because("a slice relies only on the shared domain and ports, never on another slice")
                .check(CLUB_MANAGEMENT);
    }

    @Test
    void competition_should_not_depend_on_clubmanagement() {
        noClasses().that().resideInAPackage("competition..")
                .should().dependOnClassesThat().resideInAPackage("clubmanagement..")
                .because("contexts share their JVM, never their model: until an anticorruption adapter exists, "
                        + "competition ignores clubmanagement (ADR 0027)")
                .check(COMPETITION);
    }

    @Test
    void clubmanagement_should_not_depend_on_competition() {
        noClasses().that().resideInAPackage("clubmanagement..")
                .should().dependOnClassesThat().resideInAPackage("competition..")
                .because("clubmanagement is upstream of competition and never depends on it (ADR 0027)")
                .check(CLUB_MANAGEMENT);
    }

    @Test
    void competition_slices_should_not_depend_on_each_other() {
        slices().matching("competition.(*)..")
                .should().notDependOnEachOther()
                .ignoreDependency(alwaysTrue(), resideInAnyPackage("competition.domain..", "competition.ports.."))
                .because("a slice relies only on the shared domain and ports, never on another slice")
                .check(COMPETITION);
    }
}
