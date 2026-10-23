package clubmanagement.ports;

import clubmanagement.domain.member.vo.EloRating;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.member.vo.FideTitle;

import java.util.Optional;

/**
 * A member of a club as the administrator reads them: the player, their rating and their FIDE title if they
 * have any, and the license that makes them a member.
 */
public record ClubMember(String firstName, String lastName, FfeId ffeId, FfeLicenseType licenseType,
                         Optional<EloRating> rating, Optional<FideTitle> title) {
}
