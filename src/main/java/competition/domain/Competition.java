package competition.domain;

/** Skeleton: each remaining accessor is driven by a unit test of CreateCompetition. */
public class Competition {
    private final CompetitionId id;
    private final String name;
    private final Season season;
    private final int boards;

    public Competition(CompetitionId id, String name, Season season, int boards) {
        this.id = id;
        this.name = name;
        this.season = season;
        this.boards = boards;
    }

    public CompetitionId id() { return id; }
    public String name() { return name; }
    public Season season() { return season; }
    public int boards() { return boards; }
}
