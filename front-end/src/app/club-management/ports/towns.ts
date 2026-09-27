import { Observable } from 'rxjs';

/** Port: the towns La Poste delivers to, written as it expects them on an address, and their postcodes. */
export abstract class Towns {
  abstract ofPostcode(postcode: string): Observable<string[]>;
  abstract postcodesOf(town: string): Observable<string[]>;
}
