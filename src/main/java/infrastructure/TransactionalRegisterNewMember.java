package infrastructure;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.Season;
import clubmanagement.domain.member.vo.EloRating;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.member.vo.FideTitle;
import clubmanagement.domain.person.vo.PersonId;
import clubmanagement.ports.ClubRelationshipRepository;
import clubmanagement.ports.PersonRepository;
import clubmanagement.registermember.RegisterNewMember;
import org.springframework.transaction.support.TransactionOperations;

import java.util.Optional;
import java.util.function.Supplier;

/** RegisterNewMember whose two saves, the player and their membership, succeed or fail together. */
class TransactionalRegisterNewMember extends RegisterNewMember {
    private final TransactionOperations transactions;

    TransactionalRegisterNewMember(PersonRepository people, ClubRelationshipRepository relationships,
                                   Season currentSeason, Supplier<PersonId> identities,
                                   TransactionOperations transactions) {
        super(people, relationships, currentSeason, identities);
        this.transactions = transactions;
    }

    @Override
    public PersonId registerNewMember(ClubId clubId, String firstName, String lastName, FfeId ffeId,
                                      FfeLicenseType licenseType, Optional<EloRating> rating,
                                      Optional<FideTitle> title) {
        return transactions.execute(
                status -> super.registerNewMember(clubId, firstName, lastName, ffeId, licenseType, rating, title));
    }
}
