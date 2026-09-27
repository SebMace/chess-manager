import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DeliveryTown, Towns } from '../ports/towns';

@Injectable()
export class HttpTowns implements Towns {
  private readonly http = inject(HttpClient);

  ofPostcode(postcode: string): Observable<string[]> {
    return this.http.get<string[]>('/api/towns', { params: { postcode } });
  }

  postcodesOf(town: string): Observable<string[]> {
    return this.http.get<string[]>('/api/postcodes', { params: { town } });
  }

  ofCommune(communeCode: string): Observable<DeliveryTown[]> {
    return this.http.get<DeliveryTown[]>('/api/towns', { params: { commune: communeCode } });
  }
}
