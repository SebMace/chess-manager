import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { PostalAddress } from '../postal-address/postal-address';
import { ClubOfCommittee, Clubs, FfeClubIdAlreadyUsed, NewClub } from '../ports/clubs';

@Injectable()
export class HttpClubs implements Clubs {
  private readonly http = inject(HttpClient);

  create(club: NewClub): Observable<void> {
    return this.http.post<void>('/api/clubs', {
      name: club.name,
      committeeCode: club.committeeCode,
      ffeClubId: club.ffeClubId,
      communeCode: club.communeCode,
      registeredOffice: postalAddress(club.registeredOffice),
      playingVenue: postalAddress(club.playingVenue),
    }).pipe(
      catchError((error: HttpErrorResponse) =>
        throwError(() => (error.status === 409 ? new FfeClubIdAlreadyUsed(club.ffeClubId) : error))),
    );
  }

  ofCommittee(committeeCode: string): Observable<ClubOfCommittee[]> {
    return this.http.get<ClubOfCommittee[]>('/api/clubs', { params: { committee: committeeCode } });
  }
}

function postalAddress(address: PostalAddress): { street: string; postcode: string; town: string } {
  return { street: address.street, postcode: address.postcode, town: address.town };
}
