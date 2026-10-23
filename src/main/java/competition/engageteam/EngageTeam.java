package competition.engageteam;

import competition.domain.ClubId;
import competition.domain.CompetitionId;
import competition.domain.Team;
import competition.ports.CompetitionRepository;

public class EngageTeam {
    private final CompetitionRepository competitions;

    public EngageTeam(CompetitionRepository competitions) {
        this.competitions = competitions;
    }

    public Team engageTeam(CompetitionId competition, ClubId club) {
        return competitions.find(competition).orElseThrow().engageTeamOf(club);
    }
}
