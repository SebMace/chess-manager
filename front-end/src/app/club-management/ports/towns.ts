import { Observable } from 'rxjs';

/** Port: the towns La Poste delivers to, written as it expects them on an address. */
export abstract class Towns {
  abstract ofPostcode(postcode: string): Observable<string[]>;
}
