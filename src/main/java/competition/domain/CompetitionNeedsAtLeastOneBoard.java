package competition.domain;

public class CompetitionNeedsAtLeastOneBoard extends RuntimeException {
    public CompetitionNeedsAtLeastOneBoard(int boards) {
        super("A competition needs at least one board, but " + boards + " were given");
    }
}
