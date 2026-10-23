import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Competitions, NewCompetition, SeasonCompetitions } from '../ports/competitions';

@Injectable()
export class HttpCompetitions implements Competitions {
  private readonly http = inject(HttpClient);

  create(competition: NewCompetition): Observable<void> {
    return this.http.post<void>('/api/competitions', {
      name: competition.name,
      organizingLeague: competition.organizingLeague,
      boards: competition.boards,
    });
  }

  ofCurrentSeason(): Observable<SeasonCompetitions> {
    return this.http.get<SeasonCompetitions>('/api/competitions');
  }
}
