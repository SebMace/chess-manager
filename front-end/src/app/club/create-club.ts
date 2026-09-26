import { Component, inject, signal } from '@angular/core';
import { Clubs, NewClub } from './clubs';

type CreationOutcome =
  | { kind: 'none' }
  | { kind: 'created'; club: string }
  | { kind: 'failed' };

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
              <label for="ffe-club-id">Identifiant FFE</label>
              <input id="ffe-club-id" #ffeClubId placeholder="ex. G45001" />
            </div>
            <div class="field field--wide">
              <label for="commune">Commune</label>
              <input id="commune" #commune placeholder="ex. Orléans" />
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
      }
    </section>
  `,
})
export class CreateClub {
  private readonly clubs = inject(Clubs);
  protected readonly outcome = signal<CreationOutcome>({ kind: 'none' });
  protected readonly committeeRequired = signal(false);

  protected create(event: Event, club: NewClub): void {
    event.preventDefault();
    if (!club.committeeCode) {
      this.committeeRequired.set(true);
      return;
    }
    this.committeeRequired.set(false);
    this.clubs.create(club).subscribe({
      next: () => this.outcome.set({ kind: 'created', club: club.name }),
      error: () => this.outcome.set({ kind: 'failed' }),
    });
  }
}
