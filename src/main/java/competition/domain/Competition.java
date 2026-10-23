package competition.domain;

public class Competition {
    private final CompetitionId id;
    private final String name;
    private final Season season;
    private final int boards;
    private final League organizingLeague;

    public Competition(CompetitionId id, String name, Season season, int boards) {
        this(id, name, season, boards, null);
    }

    private Competition(CompetitionId id, String name, Season season, int boards, League organizingLeague) {
        this.id = id;
        this.name = name;
        this.season = season;
        this.boards = boards;
        this.organizingLeague = organizingLeague;
    }

    public Competition organizedBy(League league) {
        return new Competition(id, name, season, boards, league);
    }

    public CompetitionId id() { return id; }
    public String name() { return name; }
    public Season season() { return season; }
    public int boards() { return boards; }
    public League organizingLeague() { return organizingLeague; }
}
