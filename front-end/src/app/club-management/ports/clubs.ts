import { Observable } from 'rxjs';

/** What an administrator provides to create a club. */
export interface NewClub {
  name: string;
  committeeCode: string;
  ffeClubId: string;
  communeCode: string;
  registeredOfficeStreet: string;
  registeredOfficePostcode: string;
  registeredOfficeTown: string;
  playingVenueStreet: string;
  playingVenuePostcode: string;
  playingVenueTown: string;
}

/** Refusal: another club already uses this FFE identifier. */
export class FfeClubIdAlreadyUsed extends Error {
  constructor(readonly ffeClubId: string) {
    super(`The FFE identifier ${ffeClubId} is already used by another club`);
  }
}

/** What an administrator is shown of a club of a departmental committee. */
export interface ClubOfCommittee {
  name: string;
  commune: string;
  ffeClubId: string;
}

/** Port: what the club screens need from the clubs managed by Chess Manager. */
export abstract class Clubs {
  abstract create(club: NewClub): Observable<void>;
  abstract ofCommittee(committeeCode: string): Observable<ClubOfCommittee[]>;
}
