package infrastructure;

import competition.createcompetition.CreateCompetition;
import competition.domain.CompetitionId;
import competition.domain.Season;
import competition.persistence.JdbcCompetitionRepository;
import competition.ports.CompetitionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.UUID;

@Configuration
class CompetitionConfiguration {
    /** One current season for the whole application (ADR 0025), translated into the language of this context. */
    private static final Season CURRENT_SEASON = new Season(ClubConfiguration.CURRENT_SEASON.yearBegin(),
            ClubConfiguration.CURRENT_SEASON.yearEnd());

    @Bean
    CompetitionRepository competitionRepository(JdbcClient jdbc) {
        return new JdbcCompetitionRepository(jdbc);
    }

    @Bean
    CreateCompetition createCompetition(CompetitionRepository competitions) {
        return new CreateCompetition(competitions, CURRENT_SEASON, () -> new CompetitionId(UUID.randomUUID()));
    }
}
