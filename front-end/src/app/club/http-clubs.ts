import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Clubs } from './clubs';

@Injectable()
export class HttpClubs implements Clubs {
  private readonly http = inject(HttpClient);

  create(name: string, committeeCode: string): Observable<void> {
    return this.http.post<void>('/clubs', { name, committeeCode });
  }
}
