import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Competitions, NewCompetition } from '../ports/competitions';

/** Compile-only skeleton: its behavior is driven by the Pact spec of the adapter. */
@Injectable()
export class HttpCompetitions implements Competitions {
  create(_competition: NewCompetition): Observable<void> {
    throw new Error('not implemented yet');
  }
}
