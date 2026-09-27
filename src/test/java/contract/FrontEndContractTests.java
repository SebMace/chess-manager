package contract;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import clubmanagement.createclub.CreateClub;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.club.vo.FfeClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.commune.CommuneCode;
import infrastructure.ChessManagerApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/** Replays against the running back-end every interaction the front-end recorded in its Pact tests. */
@Provider("chess-manager-back")
@PactFolder("front-end/pacts")
@SpringBootTest(classes = ChessManagerApplication.class, webEnvironment = RANDOM_PORT)
@Testcontainers
class FrontEndContractTests {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    private final CreateClub createClub;

    FrontEndContractTests(@Autowired CreateClub createClub) {
        this.createClub = createClub;
    }

    @BeforeEach
    void targetTheRunningBackEnd(PactVerificationContext context, @LocalServerPort int port) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void honours_what_the_front_end_expects(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("the committee 45 has the club G45100")
    void theCommittee45HasTheClubG45100() {
        aClubOfTheCommittee45("Echiquier du Gâtinais", "G45100");
    }

    @State("a club already uses the FFE identifier G45200")
    void aClubAlreadyUsesTheFfeIdentifierG45200() {
        aClubOfTheCommittee45("Cercle de Montargis", "G45200");
    }

    /** Returns the identifier of the club, which Pact injects into the path of the interaction. */
    @State("the club G45300 is managed by the application")
    Map<String, Object> theClubG45300IsManagedByTheApplication() {
        return Map.of("clubId", aClubOfTheCommittee45("Montargis Échecs", "G45300").clubId().toString());
    }

    /** The states share one database: each creates its club under an FFE identifier of its own. */
    private ClubId aClubOfTheCommittee45(String name, String ffeClubId) {
        PostalAddress address = new PostalAddress("12 rue des Échecs", "45200", "Montargis");
        return createClub.execute(name, new CommitteeCode("45"), new FfeClubId(ffeClubId), new CommuneCode("45208"),
                address, address);
    }
}
