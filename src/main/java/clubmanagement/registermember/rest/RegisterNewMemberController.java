package clubmanagement.registermember.rest;

import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.member.vo.EloRating;
import clubmanagement.domain.member.vo.FfeId;
import clubmanagement.domain.member.vo.FfeLicenseType;
import clubmanagement.domain.member.vo.FideTitle;
import clubmanagement.registermember.RegisterNewMember;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
public class RegisterNewMemberController {
    private final RegisterNewMember registerNewMember;

    public RegisterNewMemberController(RegisterNewMember registerNewMember) {
        this.registerNewMember = registerNewMember;
    }

    @PostMapping("/clubs/{club}/members")
    public ResponseEntity<Void> register(@PathVariable UUID club, @RequestBody RegisterNewMemberRequest request) {
        registerNewMember.registerNewMember(new ClubId(club), request.firstName(), request.lastName(),
                new FfeId(request.ffeId()), FfeLicenseType.valueOf(request.licenseType()),
                Optional.ofNullable(request.elo()).map(EloRating::new),
                Optional.ofNullable(request.fideTitle()).map(FideTitle::valueOf));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> refuseInvalidMember() {
        return ResponseEntity.badRequest().build();
    }

    public record RegisterNewMemberRequest(String firstName, String lastName, String ffeId, String licenseType,
                                           Integer elo, String fideTitle) {
    }
}
