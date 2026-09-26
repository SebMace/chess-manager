import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Clubs, NewClub } from './clubs';

@Injectable()
export class HttpClubs implements Clubs {
  private readonly http = inject(HttpClient);

  create(club: NewClub): Observable<void> {
    return this.http.post<void>('/clubs', {
      name: club.name,
      committeeCode: club.committeeCode,
      ...(club.ffeClubId ? { ffeClubId: club.ffeClubId } : {}),
      ...(club.commune ? { commune: club.commune } : {}),
    });
  }
}
