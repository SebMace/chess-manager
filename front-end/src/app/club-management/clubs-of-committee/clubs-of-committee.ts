import { Component, inject, signal } from '@angular/core';
import { ClubOfCommittee, Clubs } from '../ports/clubs';

@Component({
  selector: 'app-clubs-of-committee',
  styleUrl: './clubs-of-committee.css',
  template: `
    <section class="card" aria-labelledby="clubs-of-committee-title">
      <h2 id="clubs-of-committee-title">Clubs gérés par l'application</h2>
      <form class="search" novalidate (submit)="consult($event, committeeCode.value)">
        <div class="field">
          <label for="committee-code">Numéro de votre département</label>
          <input id="committee-code" #committeeCode placeholder="ex. 45" />
        </div>
        <button type="submit">Afficher les clubs</button>
      </form>
      @if (shownClubs(); as clubs) {
        @if (clubs.length === 0) {
          <p class="notice" role="status">
            Aucun club n'est géré par l'application dans ce comité.
          </p>
        } @else {
          <div class="table-scroll">
            <table>
              <thead>
                <tr>
                  <th scope="col">Club</th>
                  <th scope="col">Commune</th>
                  <th scope="col">Identifiant FFE</th>
                </tr>
              </thead>
              <tbody>
                @for (club of clubs; track club.ffeClubId) {
                  <tr>
                    <th scope="row">{{ club.name }}</th>
                    <td>{{ club.commune }}</td>
                    <td>{{ club.ffeClubId }}</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }
      }
    </section>
  `,
})
export class ClubsOfCommittee {
  private readonly clubs = inject(Clubs);
  protected readonly shownClubs = signal<readonly ClubOfCommittee[] | undefined>(undefined);

  protected consult(event: Event, committeeCode: string): void {
    event.preventDefault();
    this.clubs.ofCommittee(committeeCode).subscribe((clubs) => this.shownClubs.set(clubs));
  }
}
