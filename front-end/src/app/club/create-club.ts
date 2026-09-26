import { Component, inject, signal } from '@angular/core';
import { Clubs, NewClub } from './clubs';

type CreationOutcome =
  | { kind: 'none' }
  | { kind: 'created'; club: string }
  | { kind: 'failed' };

@Component({
  selector: 'app-create-club',
  template: `
    <form
      (submit)="create($event, {
        name: clubName.value,
        committeeCode: committeeCode.value,
        ffeClubId: ffeClubId.value,
        commune: commune.value,
      })"
    >
      <label for="club-name">Nom du club</label>
      <input id="club-name" #clubName />
      <label for="committee-code">Code du comité</label>
      <input
        id="committee-code"
        #committeeCode
        [attr.aria-describedby]="committeeRequired() ? 'committee-code-error' : null"
      />
      @if (committeeRequired()) {
        <p id="committee-code-error">Le code du comité est obligatoire.</p>
      }
      <label for="ffe-club-id">Identifiant FFE</label>
      <input id="ffe-club-id" #ffeClubId />
      <label for="commune">Commune</label>
      <input id="commune" #commune />
      <button type="submit">Créer le club</button>
    </form>
    @let result = outcome();
    @if (result.kind === 'created') {
      <p role="status">Le club {{ result.club }} a été créé.</p>
    } @else if (result.kind === 'failed') {
      <p role="status">Le club n'a pas pu être créé. Réessayez.</p>
    }
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
