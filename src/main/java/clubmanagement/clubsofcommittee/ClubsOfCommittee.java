package clubmanagement.clubsofcommittee;

import clubmanagement.domain.club.Club;
import clubmanagement.domain.club.vo.CommitteeCode;
import clubmanagement.domain.commune.Commune;
import clubmanagement.ports.ClubRepository;
import clubmanagement.ports.Communes;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** The clubs managed by the application in a departmental committee. */
public class ClubsOfCommittee {
    // The primary strength ignores accents and capitals: "échiquier" comes between "Cercle" and "U.S.".
    private static final Comparator<ClubOfCommittee> BY_NAME =
            Comparator.comparing(ClubOfCommittee::name, frenchIgnoringAccentsAndCapitals());
    private final ClubRepository clubs;
    private final Communes communes;

    public ClubsOfCommittee(ClubRepository clubs, Communes communes) {
        this.clubs = clubs;
        this.communes = communes;
    }

    public List<ClubOfCommittee> execute(CommitteeCode committee) {
        return clubs.inCommittee(committee).stream().map(this::shown).sorted(BY_NAME).toList();
    }

    private static Collator frenchIgnoringAccentsAndCapitals() {
        Collator collator = Collator.getInstance(Locale.FRENCH);
        collator.setStrength(Collator.PRIMARY);
        return collator;
    }

    // A club of a committee was created with its FFE identifier, in a commune of the reference.
    private ClubOfCommittee shown(Club club) {
        return new ClubOfCommittee(club.name(),
                communes.find(club.commune()).map(Commune::name).orElseThrow(),
                club.ffeClubId().orElseThrow());
    }
}
