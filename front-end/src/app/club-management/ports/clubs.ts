import { Observable } from 'rxjs';
import { PostalAddress } from '../postal-address/postal-address';

/** What an administrator provides to create a club. */
export interface NewClub {
  name: string;
  committeeCode: string;
  ffeClubId: string;
  communeCode: string;
  registeredOffice: PostalAddress;
  playingVenue: PostalAddress;
}

/** Refusal: another club already uses this FFE identifier. */
export class FfeClubIdAlreadyUsed extends Error {
  constructor(readonly ffeClubId: string) {
    super(`The FFE identifier ${ffeClubId} is already used by another club`);
  }
}

/** What an administrator is shown of a club of a departmental committee. */
export interface ClubOfCommittee {
  id: string;
  name: string;
  commune: string;
  ffeClubId: string;
}

/** Port: what the club screens need from the clubs managed by Chess Manager. */
export abstract class Clubs {
  abstract create(club: NewClub): Observable<void>;
  abstract ofCommittee(committeeCode: string): Observable<ClubOfCommittee[]>;
}
