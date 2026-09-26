import { Commune } from './communes';

/** The communes an administrator is looking for, given the letters typed so far. */
export function communesMatching(communes: readonly Commune[], typed: string): Commune[] {
  const letters = typed.toLowerCase();
  return letters ? communes.filter(commune => commune.name.toLowerCase().startsWith(letters)) : [];
}
