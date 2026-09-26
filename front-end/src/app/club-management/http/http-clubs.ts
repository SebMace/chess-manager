import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { Clubs, FfeClubIdAlreadyUsed, NewClub } from '../ports/clubs';

@Injectable()
export class HttpClubs implements Clubs {
  private readonly http = inject(HttpClient);

  create(club: NewClub): Observable<void> {
    return this.http.post<void>('/clubs', {
      name: club.name,
      committeeCode: club.committeeCode,
      ffeClubId: club.ffeClubId,
      communeCode: club.communeCode,
      registeredOffice: {
        street: club.registeredOfficeStreet,
        postcode: club.registeredOfficePostcode,
        town: club.registeredOfficeTown,
      },
      playingVenue: {
        street: club.playingVenueStreet,
        postcode: club.playingVenuePostcode,
        town: club.playingVenueTown,
      },
    }).pipe(
      catchError((error: HttpErrorResponse) =>
        throwError(() => (error.status === 409 ? new FfeClubIdAlreadyUsed(club.ffeClubId) : error))),
    );
  }
}
