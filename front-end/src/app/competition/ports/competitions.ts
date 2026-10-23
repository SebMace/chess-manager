import { Observable } from 'rxjs';

/** What an administrator provides to create a competition. */
export interface NewCompetition {
  name: string;
  organizingLeague: string;
  boards: number;
}

/** Port: what the competition screens need from the competitions managed by Chess Manager. */
export abstract class Competitions {
  abstract create(competition: NewCompetition): Observable<void>;
}
