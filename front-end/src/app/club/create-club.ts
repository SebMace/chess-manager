import { Component, inject, signal } from '@angular/core';
import { ClubApi } from './club-api';

@Component({
  selector: 'app-create-club',
  template: `
    <form (submit)="create($event, clubName.value)">
      <label for="club-name">Nom du club</label>
      <input id="club-name" #clubName />
      <button type="submit">Créer le club</button>
    </form>
    @if (createdClub(); as name) {
      <p role="status">Le club {{ name }} a été créé.</p>
    }
    @if (creationFailed()) {
      <p role="status">Le club n'a pas pu être créé. Réessayez.</p>
    }
  `,
})
export class CreateClub {
  private readonly clubs = inject(ClubApi);
  protected readonly createdClub = signal<string | null>(null);
  protected readonly creationFailed = signal(false);

  protected create(event: Event, name: string): void {
    event.preventDefault();
    this.clubs.create(name).subscribe({
      next: () => {
        this.createdClub.set(name);
        this.creationFailed.set(false);
      },
      error: () => {
        this.creationFailed.set(true);
        this.createdClub.set(null);
      },
    });
  }
}
