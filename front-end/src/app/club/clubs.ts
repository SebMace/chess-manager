import { Observable } from 'rxjs';

/** What an administrator provides to create a club. */
export interface NewClub {
  name: string;
  committeeCode: string;
  ffeClubId: string;
  commune: string;
}

/** Port: what the club screens need from the clubs managed by Chess Manager. */
export abstract class Clubs {
  abstract create(club: NewClub): Observable<void>;
}
