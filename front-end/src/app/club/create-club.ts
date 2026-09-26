import { Component, inject, signal } from '@angular/core';
import { Clubs } from './clubs';

type CreationOutcome =
  | { kind: 'none' }
  | { kind: 'created'; club: string }
  | { kind: 'failed' };

@Component({
  selector: 'app-create-club',
  template: `
    <form (submit)="create($event, clubName.value, committeeCode.value)">
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

  protected create(event: Event, name: string, committeeCode: string): void {
    event.preventDefault();
    if (!committeeCode) {
      this.committeeRequired.set(true);
      return;
    }
    this.clubs.create(name, committeeCode).subscribe({
      next: () => this.outcome.set({ kind: 'created', club: name }),
      error: () => this.outcome.set({ kind: 'failed' }),
    });
  }
}
