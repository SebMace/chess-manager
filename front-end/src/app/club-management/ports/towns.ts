import { Observable } from 'rxjs';

/** A town La Poste delivers to, as written on an address, with its postcodes. */
export interface DeliveryTown {
  name: string;
  postcodes: string[];
}

/** Port: the towns La Poste delivers to, written as it expects them on an address, and their postcodes. */
export abstract class Towns {
  abstract ofPostcode(postcode: string): Observable<string[]>;
  abstract postcodesOf(town: string): Observable<string[]>;
  abstract ofCommune(communeCode: string): Observable<DeliveryTown[]>;
}
