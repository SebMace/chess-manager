import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ClubApi {
  private readonly http = inject(HttpClient);

  create(name: string): Observable<void> {
    return this.http.post<void>('/clubs', { name });
  }
}
