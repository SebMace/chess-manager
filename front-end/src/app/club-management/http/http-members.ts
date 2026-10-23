import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Member, Members } from '../ports/members';

@Injectable()
export class HttpMembers implements Members {
  private readonly http = inject(HttpClient);

  ofClub(clubId: string): Observable<Member[]> {
    return this.http.get<Member[]>(`/api/clubs/${clubId}/members`);
  }

  register(clubId: string, member: Member): Observable<void> {
    return this.http.post<void>(`/api/clubs/${clubId}/members`, member);
  }
}
