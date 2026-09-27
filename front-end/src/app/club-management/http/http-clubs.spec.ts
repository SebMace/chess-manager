import { MatchersV3 } from '@pact-foundation/pact';
import { firstValueFrom } from 'rxjs';
import { adapterAgainst, frontEndContract } from '../../../testing/front-end-contract';
import { HttpClubs } from './http-clubs';

describe('HttpClubs', () => {
  it('fetches the clubs of a committee', () =>
    frontEndContract
      .addInteraction()
      .given('the committee 45 has the club G45100')
      .uponReceiving('a request for the clubs of a committee')
      .withRequest('GET', '/clubs', (request) => request.query({ committee: '45' }))
      .willRespondWith(200, (response) =>
        response.jsonBody(MatchersV3.eachLike({ name: 'Echiquier du Gâtinais', commune: 'Montargis', ffeClubId: 'G45100' })))
      .executeTest(async (mockServer) => {
        const clubs = await firstValueFrom(adapterAgainst(HttpClubs, mockServer.url).ofCommittee('45'));

        expect(clubs).toEqual([{ name: 'Echiquier du Gâtinais', commune: 'Montargis', ffeClubId: 'G45100' }]);
      }));
});
