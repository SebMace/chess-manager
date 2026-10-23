package clubmanagement.membersofclub.rest;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.member.vo.EloRating;
import clubmanagement.domain.member.vo.FideTitle;
import clubmanagement.membersofclub.MembersOfClub;
import clubmanagement.ports.ClubMember;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class MembersOfClubController {
    private final MembersOfClub membersOfClub;

    public MembersOfClubController(MembersOfClub membersOfClub) {
        this.membersOfClub = membersOfClub;
    }

    @GetMapping("/clubs/{club}/members")
    public List<MemberResponse> of(@PathVariable UUID club) {
        return membersOfClub.membersOf(new ClubId(club)).stream().map(MemberResponse::of).toList();
    }

    /**
     * A member without a rating has no elo: the player is not rated, which is not the same as rated zero.
     * Most members have no FIDE title.
     */
    public record MemberResponse(String firstName, String lastName, String ffeId, String licenseType, Integer elo,
                                 String fideTitle) {
        static MemberResponse of(ClubMember member) {
            return new MemberResponse(member.firstName(), member.lastName(), member.ffeId().value(),
                    member.licenseType().name(), member.rating().map(EloRating::rating).orElse(null),
                    member.title().map(FideTitle::name).orElse(null));
        }
    }
}
