import { Component, computed, inject, signal } from '@angular/core';
import { Clubs, FfeClubIdAlreadyUsed, NewClub } from './clubs';
import { Commune, Communes } from '../commune/communes';
import { communesMatching } from '../commune/commune-search';

const REQUIRED_INFORMATION = ['committeeCode', 'ffeClubId', 'communeCode'] as const satisfies readonly (keyof NewClub)[];
type RequiredInformation = (typeof REQUIRED_INFORMATION)[number];

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
    if (this.missing().size > 0) return;
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
