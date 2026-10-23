package competition.domain;

/** Skeleton: each remaining accessor is driven by a unit test of CreateCompetition. */
public class Competition {
    private final CompetitionId id;
    private final String name;

    public Competition(CompetitionId id, String name) {
        this.id = id;
        this.name = name;
    }

    public CompetitionId id() { return id; }
    public String name() { return name; }
    public Season season() { throw new UnsupportedOperationException("not implemented yet"); }
    public int boards() { throw new UnsupportedOperationException("not implemented yet"); }
}
