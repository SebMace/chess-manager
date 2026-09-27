package clubmanagement.openinghoursofclub.rest;

import clubmanagement.domain.club.vo.Activity;
import clubmanagement.domain.club.vo.ClubId;
import clubmanagement.domain.club.vo.PostalAddress;
import clubmanagement.domain.club.vo.Session;
import clubmanagement.domain.club.vo.Venue;
import clubmanagement.openinghoursofclub.OpeningHoursOfClub;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class OpeningHoursOfClubController {
    private final OpeningHoursOfClub openingHoursOfClub;

    public OpeningHoursOfClubController(OpeningHoursOfClub openingHoursOfClub) {
        this.openingHoursOfClub = openingHoursOfClub;
    }

    @GetMapping("/clubs/{club}/opening-hours")
    public List<SessionResponse> of(@PathVariable UUID club) {
        return openingHoursOfClub.execute(new ClubId(club)).stream().map(SessionResponse::of).toList();
    }

    /** A session at the playing venue of the club has no venue: it follows the club if it moves. */
    public record SessionResponse(String day, String from, String to, String activity, AddressResponse venue) {
        static SessionResponse of(Session session) {
            return new SessionResponse(session.day().name(), session.from().toString(), session.to().toString(),
                    session.activity().map(Activity::name).orElse(null), address(session.venue()));
        }

        private static AddressResponse address(Venue venue) {
            return switch (venue) {
                case Venue.Address(PostalAddress address) ->
                        new AddressResponse(address.street(), address.postcode(), address.town());
                case Venue.PlayingVenue() -> null;
            };
        }
    }

    public record AddressResponse(String street, String postcode, String town) {
    }
}
