import { Observable } from 'rxjs';

/** A commune a club can be located in, identified by its INSEE code. */
export interface Commune {
  code: string;
  name: string;
}

/** Port: the communes the club screens can offer. */
export abstract class Communes {
  abstract ofCommittee(committeeCode: string): Observable<Commune[]>;
}
