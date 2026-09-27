import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OpeningHours, Session } from '../ports/opening-hours';

@Injectable()
export class HttpOpeningHours implements OpeningHours {
  private readonly http = inject(HttpClient);

  of(clubId: string): Observable<Session[]> {
    return this.http.get<Session[]>(`/api/clubs/${clubId}/opening-hours`);
  }

  define(clubId: string, sessions: Session[]): Observable<void> {
    return this.http.put<void>(`/api/clubs/${clubId}/opening-hours`, sessions);
  }
}
