import { Component, computed, inject, signal } from '@angular/core';
import { Clubs, FfeClubIdAlreadyUsed, NewClub } from './clubs';
import { Commune, Communes } from '../commune/communes';
import { communesMatching } from '../commune/commune-search';

const REQUIRED_INFORMATION = [
  'committeeCode',
  'ffeClubId',
  'communeCode',
  'registeredOfficeStreet',
  'registeredOfficePostcode',
  'registeredOfficeTown',
] as const satisfies readonly (keyof NewClub)[];
type RequiredInformation = (typeof REQUIRED_INFORMATION)[number];

/** A French postcode is made of five digits; spaces typed by the administrator are ignored. */
function isPostcode(typed: string): boolean {
  return /^\d{5}$/.test(typed.replaceAll(' ', ''));
}

type CreationOutcome =
  | { kind: 'none' }
  | { kind: 'created'; club: string }
  | { kind: 'failed' }
  | { kind: 'ffeClubIdAlreadyUsed'; ffeClubId: string };

@Component({
  selector: 'app-create-club',
  styleUrl: './create-club.css',
  template: `
    <section class="card" aria-labelledby="create-club-title">
      <h2 id="create-club-title">Créer un club</h2>
      <form
        novalidate
        (submit)="create($event, {
          name: clubName.value,
          committeeCode: committeeCode.value,
          ffeClubId: ffeClubId.value,
          communeCode: chosenCommune()?.code ?? '',
          registeredOfficeStreet: officeStreet.value,
          registeredOfficePostcode: officePostcode.value,
          registeredOfficeTown: officeTown.value,
          playingVenueStreet: venueAtOffice() ? officeStreet.value : venue().street,
          playingVenuePostcode: venueAtOffice() ? officePostcode.value : venue().postcode,
          playingVenueTown: venueAtOffice() ? officeTown.value : venue().town,
        })"
      >
        <fieldset>
          <legend>Identité</legend>
          <div class="fields">
            <div class="field field--wide">
              <label for="club-name">Nom du club</label>
              <input id="club-name" #clubName autocomplete="organization" placeholder="ex. U.S. Orléans.Echecs" />
            </div>
            <div class="field">
              <label for="committee-code" class="required">Code du comité</label>
              <input
                id="committee-code"
                #committeeCode
                aria-required="true"
                placeholder="ex. 45"
                [attr.aria-invalid]="missing().has('committeeCode') || null"
                [attr.aria-describedby]="missing().has('committeeCode') ? 'committee-code-error' : null"
              />
              @if (missing().has('committeeCode')) {
                <p id="committee-code-error" class="field-error">Le code du comité est obligatoire.</p>
              }
            </div>
            <div class="field">
              <label for="ffe-club-id" class="required">Identifiant FFE</label>
              <input
                id="ffe-club-id"
                #ffeClubId
                aria-required="true"
                placeholder="ex. G45001"
                [attr.aria-invalid]="missing().has('ffeClubId') || null"
                [attr.aria-describedby]="missing().has('ffeClubId') ? 'ffe-club-id-error' : null"
              />
              @if (missing().has('ffeClubId')) {
                <p id="ffe-club-id-error" class="field-error">L'identifiant FFE est obligatoire.</p>
              }
            </div>
            <div class="field field--wide commune">
              <label for="commune" class="required">Commune</label>
              <input
                id="commune"
                #commune
                role="combobox"
                aria-required="true"
                aria-autocomplete="list"
                aria-controls="commune-options"
                autocomplete="off"
                placeholder="ex. Orléans"
                [attr.aria-expanded]="offeredCommunes().length > 0"
                [attr.aria-activedescendant]="activeCommune() >= 0 ? 'commune-option-' + activeCommune() : null"
                [attr.aria-invalid]="missing().has('communeCode') || null"
                [attr.aria-describedby]="missing().has('communeCode') ? 'commune-error' : null"
                (input)="typeCommune(commune.value, committeeCode.value)"
                (keydown)="browseCommunes($event, commune)"
              />
              <ul
                id="commune-options"
                role="listbox"
                aria-label="Communes proposées"
                class="commune-options"
                [hidden]="offeredCommunes().length === 0"
              >
                @for (offered of offeredCommunes(); track offered.code) {
                  <li
                    role="option"
                    [id]="'commune-option-' + $index"
                    [attr.aria-selected]="$index === activeCommune()"
                    (click)="chooseCommune(offered, commune)"
                  >
                    {{ offered.name }}
                  </li>
                }
              </ul>
              @if (missing().has('communeCode')) {
                <p id="commune-error" class="field-error">
                  {{ communeWritten() ? 'Choisissez la commune parmi celles proposées.' : 'La commune est obligatoire.' }}
                </p>
              }
            </div>
          </div>
        </fieldset>
        <fieldset>
          <legend>Siège social</legend>
          <div class="fields">
            <div class="field field--wide">
              <label for="office-street" class="required">Numéro et voie</label>
              <input
                id="office-street"
                #officeStreet
                aria-required="true"
                autocomplete="address-line1"
                placeholder="ex. 12 rue des Échecs"
                [attr.aria-invalid]="missing().has('registeredOfficeStreet') || null"
                [attr.aria-describedby]="missing().has('registeredOfficeStreet') ? 'office-street-error' : null"
              />
              @if (missing().has('registeredOfficeStreet')) {
                <p id="office-street-error" class="field-error">Le numéro et la voie sont obligatoires.</p>
              }
            </div>
            <div class="field">
              <label for="office-postcode" class="required">Code postal</label>
              <input
                id="office-postcode"
                #officePostcode
                aria-required="true"
                autocomplete="postal-code"
                inputmode="numeric"
                placeholder="ex. 45000"
                [attr.aria-invalid]="missing().has('registeredOfficePostcode') || postcodeMalformed() || null"
                [attr.aria-describedby]="missing().has('registeredOfficePostcode') || postcodeMalformed() ? 'office-postcode-error' : null"
              />
              @if (missing().has('registeredOfficePostcode')) {
                <p id="office-postcode-error" class="field-error">Le code postal est obligatoire.</p>
              } @else if (postcodeMalformed()) {
                <p id="office-postcode-error" class="field-error">Le code postal doit comporter 5 chiffres.</p>
              }
            </div>
            <div class="field">
              <label for="office-town" class="required">Localité</label>
              <input
                id="office-town"
                #officeTown
                aria-required="true"
                autocomplete="address-level2"
                placeholder="ex. Orléans"
                [attr.aria-invalid]="missing().has('registeredOfficeTown') || null"
                [attr.aria-describedby]="missing().has('registeredOfficeTown') ? 'office-town-error' : null"
              />
              @if (missing().has('registeredOfficeTown')) {
                <p id="office-town-error" class="field-error">La localité est obligatoire.</p>
              }
            </div>
          </div>
        </fieldset>
        <fieldset>
          <legend>Salle de jeu</legend>
          <div class="fields">
            <div class="field field--wide field--check">
              <input
                id="venue-at-office"
                type="checkbox"
                #venueAtOfficeBox
                [checked]="venueAtOffice()"
                (change)="venueAtOffice.set(venueAtOfficeBox.checked)"
              />
              <label for="venue-at-office">La salle de jeu est au siège social</label>
            </div>
            @if (!venueAtOffice()) {
              <div class="field field--wide">
                <label for="venue-street">Numéro et voie</label>
                <input
                  id="venue-street"
                  #venueStreet
                  placeholder="ex. 5 rue du Roi"
                  [value]="venue().street"
                  (input)="describeVenue({ street: venueStreet.value })"
                />
              </div>
              <div class="field">
                <label for="venue-postcode">Code postal</label>
                <input
                  id="venue-postcode"
                  #venuePostcode
                  inputmode="numeric"
                  placeholder="ex. 45100"
                  [value]="venue().postcode"
                  (input)="describeVenue({ postcode: venuePostcode.value })"
                />
              </div>
              <div class="field">
                <label for="venue-town">Localité</label>
                <input
                  id="venue-town"
                  #venueTown
                  placeholder="ex. Orléans"
                  [value]="venue().town"
                  (input)="describeVenue({ town: venueTown.value })"
                />
              </div>
            }
          </div>
        </fieldset>
        <div class="actions">
          <button type="submit">Créer le club</button>
        </div>
      </form>
      @let result = outcome();
      @if (result.kind === 'created') {
        <p role="status" class="notice notice--success">Le club {{ result.club }} a été créé.</p>
      } @else if (result.kind === 'failed') {
        <p role="status" class="notice notice--failure">Le club n'a pas pu être créé. Réessayez.</p>
      } @else if (result.kind === 'ffeClubIdAlreadyUsed') {
        <p role="status" class="notice notice--failure">
          Un club avec l'identifiant FFE {{ result.ffeClubId }} existe déjà.
        </p>
      }
    </section>
  `,
})
export class CreateClub {
  private readonly clubs = inject(Clubs);
  protected readonly outcome = signal<CreationOutcome>({ kind: 'none' });
  protected readonly missing = signal<ReadonlySet<RequiredInformation>>(new Set());
  protected readonly postcodeMalformed = signal(false);
  protected readonly venueAtOffice = signal(false);
  protected readonly venue = signal({ street: '', postcode: '', town: '' });

  protected describeVenue(part: Partial<{ street: string; postcode: string; town: string }>): void {
    this.venue.update(venue => ({ ...venue, ...part }));
  }
  private readonly communes = inject(Communes);
  private readonly communesOfCommittee = signal<readonly Commune[]>([]);
  private committeeOfCommunes = '';
  private readonly typedCommune = signal('');
  protected readonly chosenCommune = signal<Commune | null>(null);
  protected readonly activeCommune = signal(-1);
  protected readonly communeWritten = signal(false);
  protected readonly offeredCommunes = computed(() => communesMatching(this.communesOfCommittee(), this.typedCommune()));

  protected browseCommunes(event: KeyboardEvent, field: HTMLInputElement): void {
    const offered = this.offeredCommunes();
    if (offered.length === 0) return;
    if (event.key === 'ArrowDown') {
      event.preventDefault();
      this.activeCommune.update(active => Math.min(active + 1, offered.length - 1));
    } else if (event.key === 'ArrowUp') {
      event.preventDefault();
      this.activeCommune.update(active => Math.max(active - 1, 0));
    } else if (event.key === 'Escape') {
      this.typedCommune.set('');
      this.activeCommune.set(-1);
    } else if (event.key === 'Enter' && this.activeCommune() >= 0) {
      event.preventDefault();
      this.chooseCommune(offered[this.activeCommune()], field);
    }
  }

  protected chooseCommune(commune: Commune, field: HTMLInputElement): void {
    this.chosenCommune.set(commune);
    this.typedCommune.set('');
    this.activeCommune.set(-1);
    field.value = commune.name;
  }

  protected typeCommune(typed: string, committeeCode: string): void {
    this.chosenCommune.set(null);
    this.communeWritten.set(typed.trim().length > 0);
    this.typedCommune.set(typed);
    this.activeCommune.set(-1);
    if (!committeeCode || committeeCode === this.committeeOfCommunes) return;
    this.committeeOfCommunes = committeeCode;
    this.communes.ofCommittee(committeeCode).subscribe(communes => this.communesOfCommittee.set(communes));
  }

  protected create(event: Event, club: NewClub): void {
    event.preventDefault();
    this.missing.set(new Set(REQUIRED_INFORMATION.filter((information) => !club[information])));
    this.postcodeMalformed.set(!!club.registeredOfficePostcode && !isPostcode(club.registeredOfficePostcode));
    if (this.missing().size > 0 || this.postcodeMalformed()) return;
    this.clubs.create(club).subscribe({
      next: () => this.outcome.set({ kind: 'created', club: club.name }),
      error: (refusal: unknown) =>
        this.outcome.set(
          refusal instanceof FfeClubIdAlreadyUsed
            ? { kind: 'ffeClubIdAlreadyUsed', ffeClubId: refusal.ffeClubId }
            : { kind: 'failed' },
        ),
    });
  }
}
