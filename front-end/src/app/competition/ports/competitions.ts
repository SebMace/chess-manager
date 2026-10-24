import { Observable } from 'rxjs';

/** What an administrator provides to create a competition. */
export interface NewCompetition {
  name: string;
  organizingLeague: string;
  boards: number;
}

/** A season is dated by the two years it spans: the season 2026-2027 begins in 2026 and ends in 2027. */
export interface Season {
  yearBegin: number;
  yearEnd: number;
}

/** A competition as the list of a season shows it. */
export interface CompetitionOfSeason {
  name: string;
  organizingLeague: string;
  boards: number;
}

/** The competitions of a season, which stays known when there is none. */
export interface SeasonCompetitions {
  season: Season;
  competitions: CompetitionOfSeason[];
}

/** Port: what the competition screens need from the competitions managed by Chess Manager. */
export abstract class Competitions {
  abstract create(competition: NewCompetition): Observable<void>;

  abstract ofCurrentSeason(): Observable<SeasonCompetitions>;
}
