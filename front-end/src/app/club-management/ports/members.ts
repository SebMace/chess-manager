import { Observable } from 'rxjs';

/** The type of the FFE license that makes a player a member of a club. */
export type LicenseType = 'A' | 'B';

/** A FIDE title, held for life: candidate master, FIDE master, international master, grandmaster. */
export type FideTitle = 'CM' | 'MF' | 'MI' | 'GMI';

/** A player who is a member of a club, as the administrator provides them and is shown them. */
export interface Member {
  firstName: string;
  lastName: string;
  ffeId: string;
  licenseType: LicenseType;
  /** The FFE rating of the player; null when they are not rated, which is not the same as rated zero. */
  elo: number | null;
  /** Null for most players: it is a mention, not a property every member has. */
  fideTitle: FideTitle | null;
}

/** Port: the members of a club for the current season. */
export abstract class Members {
  abstract ofClub(clubId: string): Observable<Member[]>;
  abstract register(clubId: string, member: Member): Observable<void>;
}
