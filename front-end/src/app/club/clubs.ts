import { Observable } from 'rxjs';

/** What an administrator provides to create a club. */
export interface NewClub {
  name: string;
  committeeCode: string;
  ffeClubId: string;
  communeCode: string;
}

/** Refusal: another club already uses this FFE identifier. */
export class FfeClubIdAlreadyUsed extends Error {
  constructor(readonly ffeClubId: string) {
    super(`The FFE identifier ${ffeClubId} is already used by another club`);
  }
}

/** Port: what the club screens need from the clubs managed by Chess Manager. */
export abstract class Clubs {
  abstract create(club: NewClub): Observable<void>;
}
