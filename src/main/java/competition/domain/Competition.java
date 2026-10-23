package competition.domain;

public class Competition {
    private final CompetitionId id;
    private final String name;
    private final League organizingLeague;
    private final Season season;
    private final int boards;

    public Competition(CompetitionId id, String name, League organizingLeague, Season season, int boards) {
        this.id = id;
        this.name = name;
        this.organizingLeague = organizingLeague;
        this.season = season;
        this.boards = boards;
    }

    public CompetitionId id() { return id; }
    public String name() { return name; }
    public League organizingLeague() { return organizingLeague; }
    public Season season() { return season; }
    public int boards() { return boards; }
}
