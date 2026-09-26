import { Commune } from '../club-management/ports/communes';

/** The communes an administrator is looking for, given the letters typed so far. */
export function communesMatching(communes: readonly Commune[], typed: string): Commune[] {
  const typedWords = words(typed);
  if (typedWords.length === 0) return [];
  return communes
    .filter(commune => {
      const nameWords = words(commune.name);
      return typedWords.every(typedWord => nameWords.some(word => begins(word, typedWord)));
    })
    .sort((one, other) => one.name.localeCompare(other.name, 'fr'));
}

// "St" is the usual abbreviation of "Saint", yet it may also begin a word such as "Strasbourg".
function begins(word: string, typedWord: string): boolean {
  return word.startsWith(typedWord) || (typedWord === 'st' && word === 'saint');
}

function words(text: string): string[] {
  return simplified(text).split(/[^a-z]+/).filter(word => word.length > 0);
}

function simplified(text: string): string {
  return text.normalize('NFD').replace(/\p{Diacritic}/gu, '').toLowerCase();
}
