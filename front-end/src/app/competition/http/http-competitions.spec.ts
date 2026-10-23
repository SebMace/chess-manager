import { MatchersV3 } from '@pact-foundation/pact';
import { firstValueFrom } from 'rxjs';
import { adapterAgainst, frontEndContract } from '../../../testing/front-end-contract';
import { CompetitionOfSeason, NewCompetition, Season } from '../ports/competitions';
import { HttpCompetitions } from './http-competitions';

const NATIONALE_4: NewCompetition = { name: 'Nationale 4', organizingLeague: 'Centre-Val de Loire', boards: 6 };
const CURRENT_SEASON: Season = { yearBegin: 2026, yearEnd: 2027 };

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

  it('fetches the competitions of the current season with that season', () =>
    frontEndContract
      .addInteraction()
      .given('the competition Nationale 4 of the Centre-Val de Loire league is created for the current season')
      .uponReceiving('a request for the competitions of the current season')
      .withRequest('GET', '/api/competitions')
      .willRespondWith(200, (response) =>
        response.jsonBody({
          season: {
            yearBegin: MatchersV3.integer(CURRENT_SEASON.yearBegin),
            yearEnd: MatchersV3.integer(CURRENT_SEASON.yearEnd),
          },
          competitions: MatchersV3.eachLike(responseBodyOf(NATIONALE_4)),
        }),
      )
      .executeTest(async (mockServer) => {
        const seen = await firstValueFrom(adapterAgainst(HttpCompetitions, mockServer.url).ofCurrentSeason());

        expect(seen).toEqual({ season: CURRENT_SEASON, competitions: [NATIONALE_4] });
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

/** The competition as the back-end answers it, written out independently of the adapter under test. */
function responseBodyOf(competition: CompetitionOfSeason) {
  return {
    name: MatchersV3.string(competition.name),
    organizingLeague: MatchersV3.string(competition.organizingLeague),
    boards: MatchersV3.integer(competition.boards),
  };
}
