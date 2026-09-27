import { MatchersV3 } from '@pact-foundation/pact';
import { firstValueFrom } from 'rxjs';
import { adapterAgainst, frontEndContract } from '../../../testing/front-end-contract';
import { FfeClubIdAlreadyUsed, NewClub } from '../ports/clubs';
import { HttpClubs } from './http-clubs';

const LOIRET_CLUB: Omit<NewClub, 'ffeClubId'> = {
  name: 'Échiquier de Montargis',
  committeeCode: '45',
  communeCode: '45208',
  registeredOffice: { street: '12 rue des Échecs', postcode: '45200', town: 'Montargis' },
  playingVenue: { street: '5 rue du Roi', postcode: '45200', town: 'Montargis' },
};

const GATINAIS_ID = '6f1c1c7e-3c1a-4b0e-9d4e-2b7a5f0c8a11';

describe('HttpClubs', () => {
  it('fetches the clubs of a committee', () =>
    frontEndContract
      .addInteraction()
      .given('the committee 45 has the club G45100')
      .uponReceiving('a request for the clubs of a committee')
      .withRequest('GET', '/api/clubs', (request) => request.query({ committee: '45' }))
      .willRespondWith(200, (response) =>
        response.jsonBody(
          MatchersV3.eachLike({
            id: MatchersV3.uuid(GATINAIS_ID),
            name: 'Echiquier du Gâtinais',
            commune: 'Montargis',
            ffeClubId: 'G45100',
          }),
        ))
      .executeTest(async (mockServer) => {
        const clubs = await firstValueFrom(adapterAgainst(HttpClubs, mockServer.url).ofCommittee('45'));

        expect(clubs).toEqual([{ id: GATINAIS_ID, name: 'Echiquier du Gâtinais', commune: 'Montargis', ffeClubId: 'G45100' }]);
      }));

  it('creates a club', () =>
    frontEndContract
      .addInteraction()
      .uponReceiving('the creation of a club')
      .withRequest('POST', '/api/clubs', (request) => request.jsonBody(requestBodyOf({ ...LOIRET_CLUB, ffeClubId: 'G45201' })))
      .willRespondWith(201)
      .executeTest(async (mockServer) => {
        await expect(firstValueFrom(adapterAgainst(HttpClubs, mockServer.url).create({ ...LOIRET_CLUB, ffeClubId: 'G45201' })))
          .resolves.toBeNull();
      }));

  it('is told that another club already uses the FFE identifier', () =>
    frontEndContract
      .addInteraction()
      .given('a club already uses the FFE identifier G45200')
      .uponReceiving('the creation of a club with an FFE identifier already used')
      .withRequest('POST', '/api/clubs', (request) => request.jsonBody(requestBodyOf({ ...LOIRET_CLUB, ffeClubId: 'G45200' })))
      .willRespondWith(409)
      .executeTest(async (mockServer) => {
        await expect(firstValueFrom(adapterAgainst(HttpClubs, mockServer.url).create({ ...LOIRET_CLUB, ffeClubId: 'G45200' })))
          .rejects.toEqual(new FfeClubIdAlreadyUsed('G45200'));
      }));
});

/** The body the back-end expects, written out independently of the adapter under test. */
function requestBodyOf(club: NewClub) {
  return {
    name: club.name,
    committeeCode: club.committeeCode,
    ffeClubId: club.ffeClubId,
    communeCode: club.communeCode,
    registeredOffice: club.registeredOffice,
    playingVenue: club.playingVenue,
  };
}
