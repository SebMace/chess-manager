import { Commune } from './communes';

/** The communes an administrator is looking for, given the letters typed so far. */
export function communesMatching(communes: readonly Commune[], typed: string): Commune[] {
  const letters = simplified(typed);
  return letters ? communes.filter(commune => simplified(commune.name).startsWith(letters)) : [];
}

function simplified(text: string): string {
  return text.normalize('NFD').replace(/\p{Diacritic}/gu, '').toLowerCase();
}
