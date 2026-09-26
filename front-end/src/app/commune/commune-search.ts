import { Commune } from './communes';

/** The communes an administrator is looking for, given the letters typed so far. */
export function communesMatching(communes: readonly Commune[], typed: string): Commune[] {
  const letters = simplified(typed);
  return letters ? communes.filter(commune => words(commune.name).some(word => word.startsWith(letters))) : [];
}

function words(name: string): string[] {
  return simplified(name).split(/[^a-z]+/);
}

function simplified(text: string): string {
  return text.normalize('NFD').replace(/\p{Diacritic}/gu, '').toLowerCase();
}
