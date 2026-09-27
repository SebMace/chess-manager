import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Day, OpeningHours, Session } from '../ports/opening-hours';

/** A session as the administrator is typing it. */
interface SessionDraft {
  day: Day;
  from: string;
  to: string;
  activity: string;
  atPlayingVenue: boolean;
  street: string;
  postcode: string;
  town: string;
}

const DAYS: readonly { code: Day; name: string }[] = [
  { code: 'MONDAY', name: 'Lundi' },
  { code: 'TUESDAY', name: 'Mardi' },
  { code: 'WEDNESDAY', name: 'Mercredi' },
  { code: 'THURSDAY', name: 'Jeudi' },
  { code: 'FRIDAY', name: 'Vendredi' },
  { code: 'SATURDAY', name: 'Samedi' },
  { code: 'SUNDAY', name: 'Dimanche' },
];

const PROPOSED_ACTIVITIES = ['jeu libre', 'cours adultes', 'cours enfants'];

@Component({
  selector: 'app-define-opening-hours',
  imports: [RouterLink],
  styleUrl: './define-opening-hours.css',
  template: `
    <section class="card" aria-labelledby="opening-hours-title">
      <h2 id="opening-hours-title">Horaires d'ouverture du club</h2>
      @if (sessions(); as sessions) {
        <form novalidate (submit)="save($event)">
          <datalist id="proposed-activities">
            @for (activity of proposedActivities; track activity) {
              <option [value]="activity"></option>
            }
          </datalist>
          @if (sessions.length === 0) {
            <p class="notice">Aucune session n'est définie pour ce club.</p>
          }
          @for (session of sessions; track $index; let i = $index) {
            <fieldset>
              <legend>Session {{ i + 1 }}</legend>
              <div class="fields">
                <div class="field">
                  <label [for]="'day-' + i">Jour</label>
                  <select [id]="'day-' + i" (change)="update(i, { day: dayOf($event) })">
                    @for (day of days; track day.code) {
                      <option [value]="day.code" [selected]="day.code === session.day">{{ day.name }}</option>
                    }
                  </select>
                </div>
                <div class="field">
                  <label [for]="'activity-' + i">Activité</label>
                  <input [id]="'activity-' + i" list="proposed-activities" placeholder="ex. jeu libre"
                         [value]="session.activity" (input)="update(i, { activity: valueOf($event) })" />
                </div>
                <div class="field">
                  <label [for]="'from-' + i">De</label>
                  <input [id]="'from-' + i" type="time" [value]="session.from"
                         (input)="update(i, { from: valueOf($event) })" />
                </div>
                <div class="field">
                  <label [for]="'to-' + i">À</label>
                  <input [id]="'to-' + i" type="time" [value]="session.to"
                         (input)="update(i, { to: valueOf($event) })" />
                </div>
                <div class="field field--wide field--check">
                  <input [id]="'at-playing-venue-' + i" type="checkbox" [checked]="session.atPlayingVenue"
                         (change)="update(i, { atPlayingVenue: checkedOf($event) })" />
                  <label [for]="'at-playing-venue-' + i">À la salle de jeu du club</label>
                </div>
                @if (!session.atPlayingVenue) {
                  <div class="field field--wide">
                    <label [for]="'street-' + i">Numéro et voie</label>
                    <input [id]="'street-' + i" [value]="session.street"
                           (input)="update(i, { street: valueOf($event) })" />
                  </div>
                  <div class="field">
                    <label [for]="'postcode-' + i">Code postal</label>
                    <input [id]="'postcode-' + i" inputmode="numeric" [value]="session.postcode"
                           (input)="update(i, { postcode: valueOf($event) })" />
                  </div>
                  <div class="field">
                    <label [for]="'town-' + i">Localité</label>
                    <input [id]="'town-' + i" [value]="session.town"
                           (input)="update(i, { town: valueOf($event) })" />
                  </div>
                }
              </div>
              <button type="button" class="secondary" [attr.aria-label]="'Retirer la session ' + (i + 1)"
                      (click)="remove(i)">Retirer</button>
            </fieldset>
          }
          <div class="actions">
            <button type="button" class="secondary" (click)="add()">Ajouter une session</button>
            <button type="submit">Enregistrer les horaires</button>
          </div>
          @if (outcome() === 'saved') {
            <p class="notice notice--success" role="status">Les horaires d'ouverture ont été enregistrés.</p>
          } @else if (outcome() === 'refused') {
            <p class="notice notice--failure" role="alert">
              Les horaires d'ouverture n'ont pas été enregistrés : vérifiez le jour, les heures et l'adresse de
              chaque session.
            </p>
          }
        </form>
      }
      <a routerLink="/">Retour aux clubs</a>
    </section>
  `,
})
export class DefineOpeningHours {
  private readonly openingHours = inject(OpeningHours);
  private readonly clubId = inject(ActivatedRoute).snapshot.paramMap.get('id') ?? '';
  protected readonly days = DAYS;
  protected readonly proposedActivities = PROPOSED_ACTIVITIES;
  protected readonly sessions = signal<readonly SessionDraft[] | undefined>(undefined);
  protected readonly outcome = signal<'saved' | 'refused' | undefined>(undefined);

  constructor() {
    this.openingHours.of(this.clubId).subscribe((sessions) => this.sessions.set(sessions.map(draftOf)));
  }

  protected add(): void {
    this.change((sessions) => [...sessions, emptyDraft()]);
  }

  protected remove(index: number): void {
    this.change((sessions) => sessions.filter((_, i) => i !== index));
  }

  protected update(index: number, typed: Partial<SessionDraft>): void {
    this.change((sessions) => sessions.map((session, i) => (i === index ? { ...session, ...typed } : session)));
  }

  protected save(event: Event): void {
    event.preventDefault();
    this.openingHours.define(this.clubId, (this.sessions() ?? []).map(sessionOf)).subscribe({
      next: () => this.outcome.set('saved'),
      error: () => this.outcome.set('refused'),
    });
  }

  protected valueOf(event: Event): string {
    return (event.target as HTMLInputElement).value;
  }

  protected dayOf(event: Event): Day {
    return (event.target as HTMLSelectElement).value as Day;
  }

  protected checkedOf(event: Event): boolean {
    return (event.target as HTMLInputElement).checked;
  }

  private change(edit: (sessions: readonly SessionDraft[]) => readonly SessionDraft[]): void {
    this.sessions.update((sessions) => edit(sessions ?? []));
    this.outcome.set(undefined);
  }
}

function emptyDraft(): SessionDraft {
  return { day: 'MONDAY', from: '', to: '', activity: '', atPlayingVenue: true, street: '', postcode: '', town: '' };
}

function draftOf(session: Session): SessionDraft {
  return {
    day: session.day,
    from: session.from,
    to: session.to,
    activity: session.activity ?? '',
    atPlayingVenue: session.venue === null,
    street: session.venue?.street ?? '',
    postcode: session.venue?.postcode ?? '',
    town: session.venue?.town ?? '',
  };
}

function sessionOf(draft: SessionDraft): Session {
  return {
    day: draft.day,
    from: draft.from,
    to: draft.to,
    activity: draft.activity.trim() === '' ? null : draft.activity.trim(),
    venue: draft.atPlayingVenue ? null : { street: draft.street, postcode: draft.postcode, town: draft.town },
  };
}
