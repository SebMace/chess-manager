package adapters.in.rest;

import application.club.CreateClub;
import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class ClubController {
    private final CreateClub createClub;

    public ClubController(CreateClub createClub) {
        this.createClub = createClub;
    }

    @PostMapping("/clubs")
    public ResponseEntity<Void> create(@RequestBody CreateClubRequest request) {
        ClubId id = request.committeeCode() == null
                ? createClub.execute(request.name())
                : createClub.execute(request.name(), new CommitteeCode(request.committeeCode()));
        return ResponseEntity.created(URI.create("/clubs/" + id.clubId())).build();
    }

    public record CreateClubRequest(String name, String committeeCode) {
    }
}
