package competition.domain;

/** Skeleton: each remaining accessor is driven by a unit test of CreateCompetition. */
public class Competition {
    private final CompetitionId id;
    private final String name;
    private final Season season;

    public Competition(CompetitionId id, String name, Season season) {
        this.id = id;
        this.name = name;
        this.season = season;
    }

    public CompetitionId id() { return id; }
    public String name() { return name; }
    public Season season() { return season; }
    public int boards() { throw new UnsupportedOperationException("not implemented yet"); }
}
