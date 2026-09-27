package clubmanagement.domain.club.vo;

public sealed interface Venue {
    Venue PLAYING_VENUE = new PlayingVenue();

    /** The current playing venue of the club: a session held there follows the club when it moves. */
    record PlayingVenue() implements Venue {
    }

    record Address(PostalAddress address) implements Venue {
    }
}
