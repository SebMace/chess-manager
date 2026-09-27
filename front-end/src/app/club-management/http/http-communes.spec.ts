import { MatchersV3 } from '@pact-foundation/pact';
import { firstValueFrom } from 'rxjs';
import { adapterAgainst, frontEndContract } from '../../../testing/front-end-contract';
import { HttpCommunes } from './http-communes';

describe('HttpCommunes', () => {
  it('fetches the communes of the department of a committee', () =>
    frontEndContract
      .addInteraction()
      .uponReceiving('a request for the communes of a committee')
      .withRequest('GET', '/api/communes', (request) => request.query({ committee: '45' }))
      .willRespondWith(200, (response) => response.jsonBody(MatchersV3.eachLike({ code: '45232', name: 'Olivet' })))
      .executeTest(async (mockServer) => {
        const communes = await firstValueFrom(adapterAgainst(HttpCommunes, mockServer.url).ofCommittee('45'));

        expect(communes).toEqual([{ code: '45232', name: 'Olivet' }]);
      }));
});
