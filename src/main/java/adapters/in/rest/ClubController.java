package adapters.in.rest;

import application.club.CommuneNotInCommitteeDepartment;
import application.club.CreateClub;
import application.club.FfeClubIdAlreadyUsed;
import domain.club.vo.ClubId;
import domain.club.vo.CommitteeCode;
import domain.club.vo.FfeClubId;
import domain.club.vo.PostalAddress;
import domain.commune.CommuneCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
        if (request.committeeCode() == null) return ResponseEntity.badRequest().build();
        ClubId id = createClub.execute(request.name(), new CommitteeCode(request.committeeCode()),
                new FfeClubId(request.ffeClubId()), new CommuneCode(request.communeCode()),
                request.registeredOffice() == null ? null : request.registeredOffice().toPostalAddress(), null);
        return ResponseEntity.created(URI.create("/clubs/" + id.clubId())).build();
    }

    @ExceptionHandler({IllegalArgumentException.class, CommuneNotInCommitteeDepartment.class})
    public ResponseEntity<Void> refuseInvalidClub() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(FfeClubIdAlreadyUsed.class)
    public ResponseEntity<Void> refuseDuplicateFfeClubId() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    public record CreateClubRequest(String name, String committeeCode, String ffeClubId, String communeCode,
                                    AddressRequest registeredOffice) {
    }

    public record AddressRequest(String street, String postcode, String town) {
        PostalAddress toPostalAddress() { return new PostalAddress(street, postcode, town); }
    }
}
