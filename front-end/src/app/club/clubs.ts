import { Observable } from 'rxjs';

/** Port: what the club screens need from the clubs managed by Chess Manager. */
export abstract class Clubs {
  abstract create(name: string, committeeCode: string): Observable<void>;
}
