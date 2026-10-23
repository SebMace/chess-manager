package competition.domain;

/** Skeleton: each remaining accessor is driven by a unit test of CreateCompetition. */
public class Competition {
    private final CompetitionId id;

    public Competition(CompetitionId id) {
        this.id = id;
    }

    public CompetitionId id() { return id; }
    public String name() { throw new UnsupportedOperationException("not implemented yet"); }
    public Season season() { throw new UnsupportedOperationException("not implemented yet"); }
    public int boards() { throw new UnsupportedOperationException("not implemented yet"); }
}
