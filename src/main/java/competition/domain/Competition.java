package competition.domain;

import java.util.ArrayList;
import java.util.List;

public class Competition {
    private final CompetitionId id;
    private final String name;
    private final League organizingLeague;
    private final Season season;
    private final int boards;
    private final List<Team> teams = new ArrayList<>();

    public Competition(CompetitionId id, String name, League organizingLeague, Season season, int boards) {
        this(id, name, organizingLeague, season, boards, List.of());
    }

    private Competition(CompetitionId id, String name, League organizingLeague, Season season, int boards,
                        List<Team> teams) {
        if (boards < 1) throw new CompetitionNeedsAtLeastOneBoard(boards);
        this.id = id;
        this.name = name;
        this.organizingLeague = organizingLeague;
        this.season = season;
        this.boards = boards;
        this.teams.addAll(teams);
    }

    /** A competition as it stood when it was saved, with the teams already engaged: no new engagement happens. */
    public static Competition reconstitute(CompetitionId id, String name, League organizingLeague, Season season,
                                           int boards, List<Team> teams) {
        return new Competition(id, name, organizingLeague, season, boards, teams);
    }

    public CompetitionId id() { return id; }
    public String name() { return name; }
    public League organizingLeague() { return organizingLeague; }
    public Season season() { return season; }
    public int boards() { return boards; }

    public Team engageTeamOf(ClubId club) {
        Team team = new Team(club, numberOfTeamsEngagedBy(club) + 1);
        teams.add(team);
        return team;
    }

    public List<Team> teams() {
        return List.copyOf(teams);
    }

    private int numberOfTeamsEngagedBy(ClubId club) {
        return (int) teams.stream().filter(team -> team.club().equals(club)).count();
    }
}
