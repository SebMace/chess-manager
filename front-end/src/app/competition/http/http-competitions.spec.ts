import { firstValueFrom } from 'rxjs';
import { adapterAgainst, frontEndContract } from '../../../testing/front-end-contract';
import { NewCompetition } from '../ports/competitions';
import { HttpCompetitions } from './http-competitions';

const NATIONALE_4: NewCompetition = { name: 'Nationale 4', organizingLeague: 'Centre-Val de Loire', boards: 6 };

describe('HttpCompetitions', () => {
  it('creates a competition', () =>
    frontEndContract
      .addInteraction()
      .uponReceiving('the creation of a competition')
      .withRequest('POST', '/api/competitions', (request) => request.jsonBody(requestBodyOf(NATIONALE_4)))
      .willRespondWith(201)
      .executeTest(async (mockServer) => {
        await expect(firstValueFrom(adapterAgainst(HttpCompetitions, mockServer.url).create(NATIONALE_4)))
          .resolves.toBeNull();
      }));
});

/** The body the back-end expects, written out independently of the adapter under test. */
function requestBodyOf(competition: NewCompetition) {
  return {
    name: competition.name,
    organizingLeague: competition.organizingLeague,
    boards: competition.boards,
  };
}
