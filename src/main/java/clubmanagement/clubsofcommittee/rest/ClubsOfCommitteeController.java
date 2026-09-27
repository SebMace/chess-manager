package clubmanagement.clubsofcommittee.rest;

import clubmanagement.clubsofcommittee.ClubsOfCommittee;
import clubmanagement.domain.club.vo.CommitteeCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ClubsOfCommitteeController {
    private final ClubsOfCommittee clubsOfCommittee;

    public ClubsOfCommitteeController(ClubsOfCommittee clubsOfCommittee) {
        this.clubsOfCommittee = clubsOfCommittee;
    }

    @GetMapping("/clubs")
    public List<ClubResponse> ofCommittee(@RequestParam String committee) {
        return clubsOfCommittee.execute(new CommitteeCode(committee)).stream()
                .map(club -> new ClubResponse(club.name(), club.commune(), club.ffeClubId().value()))
                .toList();
    }

    public record ClubResponse(String name, String commune, String ffeClubId) {
    }
}
