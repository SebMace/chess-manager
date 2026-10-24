package competition.engageteam;

import competition.domain.ClubId;
import competition.domain.Competition;
import competition.domain.CompetitionId;
import competition.domain.Team;
import competition.ports.CompetitionRepository;

public class EngageTeam {
    private final CompetitionRepository competitions;

    public EngageTeam(CompetitionRepository competitions) {
        this.competitions = competitions;
    }

    public Team engageTeam(CompetitionId competitionId, ClubId club) {
        Competition competition = competitions.find(competitionId).orElseThrow();
        Team team = competition.engageTeamOf(club);
        competitions.save(competition);
        return team;
    }
}
