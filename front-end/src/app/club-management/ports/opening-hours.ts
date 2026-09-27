import { Observable } from 'rxjs';

export type Day = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';

/** A postal address where a session takes place, when it is not the playing venue of the club. */
export interface SessionAddress {
  street: string;
  postcode: string;
  town: string;
}

/** A weekly part of the opening hours of a club; without venue, it takes place at the playing venue. */
export interface Session {
  day: Day;
  from: string;
  to: string;
  activity: string | null;
  venue: SessionAddress | null;
}

/** Port: the opening hours of a club, defined all at once. */
export abstract class OpeningHours {
  abstract of(clubId: string): Observable<Session[]>;
  abstract define(clubId: string, sessions: Session[]): Observable<void>;
}
