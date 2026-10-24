package infrastructure;

import competition.competitionsofseason.CompetitionsOfSeason;
import competition.competitionsofseason.rest.CompetitionsOfSeasonController;
import competition.createcompetition.CreateCompetition;
import competition.createcompetition.rest.CreateCompetitionController;
import competition.domain.CompetitionId;
import competition.domain.Season;
import competition.engageteam.EngageTeam;
import competition.persistence.JdbcCompetitionRepository;
import competition.ports.CompetitionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.support.TransactionOperations;

import java.util.UUID;

@Configuration
class CompetitionConfiguration {
    /** One current season for the whole application (ADR 0025), translated into the language of this context. */
    private static final Season CURRENT_SEASON = new Season(ClubConfiguration.CURRENT_SEASON.yearBegin(),
            ClubConfiguration.CURRENT_SEASON.yearEnd());

    @Bean
    CompetitionRepository competitionRepository(JdbcClient jdbc, TransactionOperations transactions) {
        return new JdbcCompetitionRepository(jdbc, transactions);
    }

    @Bean
    CreateCompetition createCompetition(CompetitionRepository competitions) {
        return new CreateCompetition(competitions, CURRENT_SEASON, () -> new CompetitionId(UUID.randomUUID()));
    }

    @Bean
    EngageTeam engageTeam(CompetitionRepository competitions) {
        return new EngageTeam(competitions);
    }

    @Bean
    CreateCompetitionController createCompetitionController(CreateCompetition createCompetition) {
        return new CreateCompetitionController(createCompetition);
    }

    @Bean
    CompetitionsOfSeason competitionsOfSeason(CompetitionRepository competitions) {
        return new CompetitionsOfSeason(competitions, CURRENT_SEASON);
    }

    @Bean
    CompetitionsOfSeasonController competitionsOfSeasonController(CompetitionsOfSeason competitionsOfSeason) {
        return new CompetitionsOfSeasonController(competitionsOfSeason);
    }
}
