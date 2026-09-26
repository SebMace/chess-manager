import { Commune } from './communes';
import { communesMatching } from './commune-search';

const ORLEANS: Commune = { code: '45234', name: 'Orléans' };
const OLIVET: Commune = { code: '45232', name: 'Olivet' };
const SAINT_PRYVE: Commune = { code: '45298', name: 'Saint-Pryvé-Saint-Mesmin' };
const LOIRET = [ORLEANS, OLIVET, SAINT_PRYVE];

describe('communesMatching', () => {
  it('finds a commune whatever the accents and capitals typed', () => {
    expect(communesMatching(LOIRET, 'ORLE')).toEqual([ORLEANS]);
  });

  it('finds a commune by the beginning of any word of its name', () => {
    expect(communesMatching(LOIRET, 'pryv')).toEqual([SAINT_PRYVE]);
  });
});
