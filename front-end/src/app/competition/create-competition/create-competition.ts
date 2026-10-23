import { Component, inject, signal } from '@angular/core';
import { Competitions, NewCompetition } from '../ports/competitions';

type Problem = 'name' | 'organizingLeague' | 'boards';

type CreationOutcome =
  | { kind: 'none' }
  | { kind: 'created'; competition: string }
  | { kind: 'failed' };

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
            <input
              id="competition-name"
              #competitionName
              aria-required="true"
              placeholder="ex. Nationale 4"
              [attr.aria-invalid]="problems().has('name') || null"
              [attr.aria-describedby]="problems().has('name') ? 'competition-name-error' : null"
            />
            @if (problems().has('name')) {
              <p id="competition-name-error" class="field-error">Le nom de la compétition est obligatoire.</p>
            }
          </div>
          <div class="field">
            <label for="organizing-league" class="required">Ligue organisatrice</label>
            <input
              id="organizing-league"
              #organizingLeague
              aria-required="true"
              placeholder="ex. Centre-Val de Loire"
              [attr.aria-invalid]="problems().has('organizingLeague') || null"
              [attr.aria-describedby]="problems().has('organizingLeague') ? 'organizing-league-error' : null"
            />
            @if (problems().has('organizingLeague')) {
              <p id="organizing-league-error" class="field-error">La ligue organisatrice est obligatoire.</p>
            }
          </div>
          <div class="field">
            <label for="boards" class="required">Nombre d'échiquiers</label>
            <input
              id="boards"
              #boards
              type="number"
              min="1"
              aria-required="true"
              placeholder="ex. 6"
              [attr.aria-invalid]="problems().has('boards') || null"
              [attr.aria-describedby]="problems().has('boards') ? 'boards-error' : null"
            />
            @if (problems().has('boards')) {
              <p id="boards-error" class="field-error">Le nombre d'échiquiers doit être un entier d'au moins 1.</p>
            }
          </div>
        </div>
        <div class="actions">
          <button type="submit">Créer la compétition</button>
        </div>
      </form>
      @let result = outcome();
      @if (result.kind === 'created') {
        <p role="status" class="notice notice--success">La compétition {{ result.competition }} a été créée.</p>
      } @else if (result.kind === 'failed') {
        <p role="status" class="notice notice--failure">La compétition n'a pas pu être créée. Réessayez.</p>
      }
    </section>
  `,
})
export class CreateCompetition {
  private readonly competitions = inject(Competitions);
  protected readonly outcome = signal<CreationOutcome>({ kind: 'none' });
  protected readonly problems = signal<ReadonlySet<Problem>>(new Set());

  protected create(event: Event, competition: NewCompetition): void {
    event.preventDefault();
    this.problems.set(problemsOf(competition));
    if (this.problems().size > 0) return;
    this.competitions.create(competition).subscribe({
      next: () => this.outcome.set({ kind: 'created', competition: competition.name }),
      error: () => this.outcome.set({ kind: 'failed' }),
    });
  }
}

function problemsOf(competition: NewCompetition): ReadonlySet<Problem> {
  const problems = new Set<Problem>();
  if (!competition.name.trim()) problems.add('name');
  if (!competition.organizingLeague.trim()) problems.add('organizingLeague');
  if (!Number.isInteger(competition.boards) || competition.boards < 1) problems.add('boards');
  return problems;
}
