import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Commune, Communes } from './communes';

@Injectable()
export class HttpCommunes implements Communes {
  private readonly http = inject(HttpClient);

  ofCommittee(committeeCode: string): Observable<Commune[]> {
    return this.http.get<Commune[]>('/communes', { params: { committee: committeeCode } });
  }
}
