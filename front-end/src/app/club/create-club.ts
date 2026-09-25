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
  `,
})
export class CreateClub {
  private readonly clubs = inject(ClubApi);
  protected readonly createdClub = signal<string | null>(null);

  protected create(event: Event, name: string): void {
    event.preventDefault();
    this.clubs.create(name).subscribe(() => this.createdClub.set(name));
  }
}
