import { Component, inject, signal } from '@angular/core';
import { Clubs, FfeClubIdAlreadyUsed, NewClub } from './clubs';

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
          commune: commune.value,
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
                [attr.aria-invalid]="committeeRequired() || null"
                [attr.aria-describedby]="committeeRequired() ? 'committee-code-error' : null"
              />
              @if (committeeRequired()) {
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
                [attr.aria-invalid]="ffeClubIdRequired() || null"
                [attr.aria-describedby]="ffeClubIdRequired() ? 'ffe-club-id-error' : null"
              />
              @if (ffeClubIdRequired()) {
                <p id="ffe-club-id-error" class="field-error">L'identifiant FFE est obligatoire.</p>
              }
            </div>
            <div class="field field--wide">
              <label for="commune" class="required">Commune</label>
              <input
                id="commune"
                #commune
                aria-required="true"
                placeholder="ex. Orléans"
                [attr.aria-invalid]="communeRequired() || null"
                [attr.aria-describedby]="communeRequired() ? 'commune-error' : null"
              />
              @if (communeRequired()) {
                <p id="commune-error" class="field-error">La commune est obligatoire.</p>
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
  protected readonly committeeRequired = signal(false);
  protected readonly ffeClubIdRequired = signal(false);
  protected readonly communeRequired = signal(false);

  protected create(event: Event, club: NewClub): void {
    event.preventDefault();
    this.committeeRequired.set(!club.committeeCode);
    this.ffeClubIdRequired.set(!club.ffeClubId);
    this.communeRequired.set(!club.commune);
    if (this.committeeRequired() || this.ffeClubIdRequired() || this.communeRequired()) return;
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
