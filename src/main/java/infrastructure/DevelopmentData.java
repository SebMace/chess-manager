package infrastructure;

import application.club.CreateClub;
import domain.club.vo.CommitteeCode;
import domain.club.vo.FfeClubId;
import domain.club.vo.PostalAddress;
import domain.commune.CommuneCode;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.simple.JdbcClient;

/**
 * A minimal set of clubs for local development, created through the use cases so that it follows
 * the domain rules. Each start empties the tables first, so that every run begins from the same data. Entirely fictitious: real club names and FFE identifiers are FFE data.
 */
class DevelopmentData implements ApplicationRunner {
    private static final CommitteeCode LOIRET = new CommitteeCode("45");

    private final JdbcClient jdbc;
    private final CreateClub createClub;

    DevelopmentData(JdbcClient jdbc, CreateClub createClub) {
        this.jdbc = jdbc;
        this.createClub = createClub;
    }

    @Override
    public void run(ApplicationArguments arguments) {
        jdbc.sql("DELETE FROM club").update();
        PostalAddress orleans = new PostalAddress("12 rue des Échecs", "45000", "Orléans");
        createClub.execute("Les Cavaliers de la Loire", LOIRET, new FfeClubId("DEMO01"), new CommuneCode("45234"),
                orleans, orleans);
        createClub.execute("Échiquier du Val", LOIRET, new FfeClubId("DEMO02"), new CommuneCode("45298"),
                new PostalAddress("3 allée des Pions", "45750", "Saint-Pryvé-Saint-Mesmin"),
                new PostalAddress("8 rue des Tours", "45160", "Olivet"));
        PostalAddress loury = new PostalAddress("3 place de l'Église", "45470", "Loury");
        createClub.execute("Tour de Loury", LOIRET, new FfeClubId("DEMO03"), new CommuneCode("45188"),
                loury, loury);
    }
}
