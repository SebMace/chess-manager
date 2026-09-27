import { TestBed } from '@angular/core/testing';
import { HttpInterceptorFn, provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { Type } from '@angular/core';
import { PactV4 } from '@pact-foundation/pact';

/**
 * The contract between the front-end and the back-end. Every HTTP adapter spec records its
 * interactions here; the back-end replays them in contract.FrontEndContractTests, which reads
 * only this consumer and provider pair.
 */
export const frontEndContract = new PactV4({
  consumer: 'chess-manager-front',
  provider: 'chess-manager-back',
  dir: 'pacts',
  logLevel: 'warn',
});

/** The adapters call relative URLs; during the test they reach the Pact mock server instead. */
const towards = (baseUrl: string): HttpInterceptorFn => (request, next) =>
  next(request.clone({ url: baseUrl + request.url }));

/** Provides an HTTP adapter whose requests reach the Pact mock server. */
export function adapterAgainst<T>(adapter: Type<T>, mockServerUrl: string): T {
  TestBed.configureTestingModule({
    providers: [adapter, provideHttpClient(withFetch(), withInterceptors([towards(mockServerUrl)]))],
  });
  return TestBed.inject(adapter);
}
