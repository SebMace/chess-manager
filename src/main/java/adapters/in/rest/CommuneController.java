package adapters.in.rest;

import application.club.CommunesOfCommittee;
import clubmanagement.domain.club.vo.CommitteeCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommuneController {
    private final CommunesOfCommittee communesOfCommittee;

    public CommuneController(CommunesOfCommittee communesOfCommittee) {
        this.communesOfCommittee = communesOfCommittee;
    }

    @GetMapping("/communes")
    public List<CommuneResponse> ofCommittee(@RequestParam String committee) {
        return communesOfCommittee.execute(new CommitteeCode(committee)).stream()
                .map(commune -> new CommuneResponse(commune.code().value(), commune.name()))
                .toList();
    }

    public record CommuneResponse(String code, String name) {
    }
}
