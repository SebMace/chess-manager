package competition.createcompetition.rest;

import competition.createcompetition.CreateCompetition;
import competition.domain.CompetitionId;
import competition.domain.CompetitionNeedsAtLeastOneBoard;
import competition.domain.League;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class CreateCompetitionController {
    private final CreateCompetition createCompetition;

    public CreateCompetitionController(CreateCompetition createCompetition) {
        this.createCompetition = createCompetition;
    }

    @PostMapping("/competitions")
    public ResponseEntity<Void> create(@RequestBody CreateCompetitionRequest request) {
        CompetitionId id = createCompetition.createCompetition(request.name(), new League(request.organizingLeague()),
                request.boards());
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}")
                .buildAndExpand(id.competitionId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @ExceptionHandler(CompetitionNeedsAtLeastOneBoard.class)
    public ResponseEntity<Void> refuseCompetitionWithoutBoard() {
        return ResponseEntity.badRequest().build();
    }

    public record CreateCompetitionRequest(String name, String organizingLeague, int boards) {
    }
}
