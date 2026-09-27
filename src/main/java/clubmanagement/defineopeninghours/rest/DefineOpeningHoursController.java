package clubmanagement.defineopeninghours.rest;

import clubmanagement.defineopeninghours.DefineOpeningHours;
import clubmanagement.domain.club.vo.Activity;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.domain.club.vo.Venue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class DefineOpeningHoursController {
    private final DefineOpeningHours defineOpeningHours;

    public DefineOpeningHoursController(DefineOpeningHours defineOpeningHours) {
        this.defineOpeningHours = defineOpeningHours;
    }

    @PutMapping("/clubs/{club}/opening-hours")
    public ResponseEntity<Void> define(@PathVariable UUID club, @RequestBody List<SessionRequest> sessions) {
        defineOpeningHours.execute(new ClubId(club),
                sessions.stream().map(SessionRequest::toSession).collect(Collectors.toSet()));
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> refuseInvalidSession() {
        return ResponseEntity.badRequest().build();
    }

    public record SessionRequest(String day, String from, String to, String activity, AddressRequest venue) {
        Session toSession() {
            return new Session(DayOfWeek.valueOf(day), LocalTime.parse(from), LocalTime.parse(to),
                    Optional.ofNullable(activity).map(Activity::new),
                    venue == null ? Venue.PLAYING_VENUE : new Venue.Address(venue.toPostalAddress()));
        }
    }

    public record AddressRequest(String street, String postcode, String town) {
        PostalAddress toPostalAddress() { return new PostalAddress(street, postcode, town); }
    }
}
