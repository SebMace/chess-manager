/** A French postal address (NF Z10-011): street line, postcode and delivery town. */
export interface PostalAddress {
  street: string;
  postcode: string;
  town: string;
}

export const NO_ADDRESS: PostalAddress = { street: '', postcode: '', town: '' };

/** What prevents an administrator from giving a postal address. */
export type AddressProblem = 'missingStreet' | 'missingPostcode' | 'malformedPostcode' | 'missingTown';

export function problemsOf(address: PostalAddress): ReadonlySet<AddressProblem> {
  const problems = new Set<AddressProblem>();
  if (!address.street) problems.add('missingStreet');
  if (!address.postcode) problems.add('missingPostcode');
  else if (!isPostcode(address.postcode)) problems.add('malformedPostcode');
  if (!address.town) problems.add('missingTown');
  return problems;
}

/** A French postcode is made of five digits; spaces typed by the administrator are ignored. */
function isPostcode(typed: string): boolean {
  return /^\d{5}$/.test(typed.replaceAll(' ', ''));
}
