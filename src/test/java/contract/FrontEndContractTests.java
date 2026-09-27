package contract;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import clubmanagement.createclub.CreateClub;
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
        PostalAddress address = new PostalAddress("12 rue des Échecs", "45200", "Montargis");
        createClub.execute("Echiquier du Gâtinais", new CommitteeCode("45"), new FfeClubId("G45100"),
                new CommuneCode("45208"), address, address);
    }
}
