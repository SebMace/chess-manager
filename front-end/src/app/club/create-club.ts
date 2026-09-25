import { Component, inject, signal } from '@angular/core';
import { ClubApi } from './club-api';

type CreationOutcome =
  | { kind: 'none' }
  | { kind: 'created'; club: string }
  | { kind: 'failed' };

@Component({
  selector: 'app-create-club',
  template: `
    <form (submit)="create($event, clubName.value)">
      <label for="club-name">Nom du club</label>
      <input id="club-name" #clubName />
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
  private readonly clubs = inject(ClubApi);
  protected readonly outcome = signal<CreationOutcome>({ kind: 'none' });

  protected create(event: Event, name: string): void {
    event.preventDefault();
    this.clubs.create(name).subscribe({
      next: () => this.outcome.set({ kind: 'created', club: name }),
      error: () => this.outcome.set({ kind: 'failed' }),
    });
  }
}
