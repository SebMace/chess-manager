import { Component } from '@angular/core';

@Component({
  selector: 'app-create-competition',
  styleUrl: './create-competition.css',
  template: `
    <section class="card" aria-labelledby="create-competition-title">
      <h2 id="create-competition-title">Créer une compétition</h2>
      <form novalidate>
        <div class="fields">
          <div class="field field--wide">
            <label for="competition-name" class="required">Nom de la compétition</label>
            <input id="competition-name" aria-required="true" placeholder="ex. Nationale 4" />
          </div>
          <div class="field">
            <label for="organizing-league" class="required">Ligue organisatrice</label>
            <input id="organizing-league" aria-required="true" placeholder="ex. Centre-Val de Loire" />
          </div>
          <div class="field">
            <label for="boards" class="required">Nombre d'échiquiers</label>
            <input id="boards" type="number" min="1" aria-required="true" placeholder="ex. 6" />
          </div>
        </div>
        <div class="actions">
          <button type="submit">Créer la compétition</button>
        </div>
      </form>
    </section>
  `,
})
export class CreateCompetition {}
