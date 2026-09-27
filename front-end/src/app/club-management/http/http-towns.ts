import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Towns } from '../ports/towns';

@Injectable()
export class HttpTowns implements Towns {
  private readonly http = inject(HttpClient);

  ofPostcode(postcode: string): Observable<string[]> {
    return this.http.get<string[]>('/towns', { params: { postcode } });
  }

  postcodesOf(town: string): Observable<string[]> {
    return this.http.get<string[]>('/postcodes', { params: { town } });
  }
}
