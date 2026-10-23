import { Component, inject, signal } from '@angular/core';
import { Competitions, NewCompetition } from '../ports/competitions';

@Component({
  selector: 'app-create-competition',
  styleUrl: './create-competition.css',
  template: `
    <section class="card" aria-labelledby="create-competition-title">
      <h2 id="create-competition-title">Créer une compétition</h2>
      <form
        novalidate
        (submit)="create($event, {
          name: competitionName.value,
          organizingLeague: organizingLeague.value,
          boards: boards.valueAsNumber,
        })"
      >
        <div class="fields">
          <div class="field field--wide">
            <label for="competition-name" class="required">Nom de la compétition</label>
            <input id="competition-name" #competitionName aria-required="true" placeholder="ex. Nationale 4" />
          </div>
          <div class="field">
            <label for="organizing-league" class="required">Ligue organisatrice</label>
            <input id="organizing-league" #organizingLeague aria-required="true" placeholder="ex. Centre-Val de Loire" />
          </div>
          <div class="field">
            <label for="boards" class="required">Nombre d'échiquiers</label>
            <input id="boards" #boards type="number" min="1" aria-required="true" placeholder="ex. 6" />
          </div>
        </div>
        <div class="actions">
          <button type="submit">Créer la compétition</button>
        </div>
      </form>
      @if (created(); as name) {
        <p role="status" class="notice notice--success">La compétition {{ name }} a été créée.</p>
      }
    </section>
  `,
})
export class CreateCompetition {
  private readonly competitions = inject(Competitions);
  protected readonly created = signal<string | null>(null);

  protected create(event: Event, competition: NewCompetition): void {
    event.preventDefault();
    this.competitions.create(competition).subscribe(() => this.created.set(competition.name));
  }
}
