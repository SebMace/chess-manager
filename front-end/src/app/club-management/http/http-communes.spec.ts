import { TestBed } from '@angular/core/testing';
import { HttpInterceptorFn, provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { MatchersV3, PactV4 } from '@pact-foundation/pact';
import { firstValueFrom } from 'rxjs';
import { HttpCommunes } from './http-communes';

const pact = new PactV4({ consumer: 'chess-manager-front', provider: 'chess-manager-back', dir: 'pacts', logLevel: 'warn' });

/** The adapter calls relative URLs; during the test they reach the Pact mock server instead. */
const towards = (baseUrl: string): HttpInterceptorFn => (request, next) =>
  next(request.clone({ url: baseUrl + request.url }));

describe('HttpCommunes', () => {
  it('fetches the communes of the department of a committee', () =>
    pact
      .addInteraction()
      .uponReceiving('a request for the communes of a committee')
      .withRequest('GET', '/communes', (request) => request.query({ committee: '45' }))
      .willRespondWith(200, (response) => response.jsonBody(MatchersV3.eachLike({ code: '45232', name: 'Olivet' })))
      .executeTest(async (mockServer) => {
        TestBed.configureTestingModule({
          providers: [HttpCommunes, provideHttpClient(withFetch(), withInterceptors([towards(mockServer.url)]))],
        });

        const communes = await firstValueFrom(TestBed.inject(HttpCommunes).ofCommittee('45'));

        expect(communes).toEqual([{ code: '45232', name: 'Olivet' }]);
      }));
});
