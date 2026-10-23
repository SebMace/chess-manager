import { Component, inject, signal } from '@angular/core';
import { Competitions, SeasonCompetitions } from '../ports/competitions';

@Component({
  selector: 'app-competitions-of-season',
  styleUrl: './competitions-of-season.css',
  template: `
    @if (shown(); as seen) {
      <section class="card" aria-labelledby="competitions-title">
        <h2 id="competitions-title">Compétitions de la saison {{ seen.season.yearBegin }}-{{ seen.season.yearEnd }}</h2>
        @if (seen.competitions.length === 0) {
          <p class="notice" role="status">
            Aucune compétition pour la saison {{ seen.season.yearBegin }}-{{ seen.season.yearEnd }}.
          </p>
        } @else {
          <div class="table-scroll">
            <table>
              <thead>
                <tr>
                  <th scope="col">Compétition</th>
                  <th scope="col">Ligue organisatrice</th>
                  <th scope="col" class="numeric">Échiquiers</th>
                </tr>
              </thead>
              <tbody>
                @for (competition of seen.competitions; track $index) {
                  <tr>
                    <th scope="row">{{ competition.name }}</th>
                    <td>{{ competition.organizingLeague }}</td>
                    <td class="numeric">{{ competition.boards }}</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }
      </section>
    }
  `,
})
export class CompetitionsOfSeason {
  protected readonly shown = signal<SeasonCompetitions | undefined>(undefined);

  constructor() {
    inject(Competitions)
      .ofCurrentSeason()
      .subscribe((seen) => this.shown.set(seen));
  }
}
