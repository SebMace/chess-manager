import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FideTitle, LicenseType, Member, Members } from '../ports/members';

const LICENSE_TYPES: readonly LicenseType[] = ['A', 'B'];

const FIDE_TITLES: readonly { code: FideTitle; name: string }[] = [
  { code: 'CM', name: 'Candidat Maître' },
  { code: 'MF', name: 'Maître FIDE' },
  { code: 'MI', name: 'Maître International' },
  { code: 'GMI', name: 'Grand Maître International' },
];

/** A member as the administrator is typing them: the rating is still the text of the field. */
interface MemberDraft {
  firstName: string;
  lastName: string;
  ffeId: string;
  licenseType: LicenseType;
  elo: string;
  fideTitle: FideTitle | '';
}

@Component({
  selector: 'app-members-of-club',
  imports: [RouterLink],
  styleUrl: './members-of-club.css',
  template: `
    <section class="card" aria-labelledby="members-title">
      <h2 id="members-title">Membres du club</h2>
      @if (shownMembers(); as members) {
        @if (members.length === 0) {
          <p class="notice" role="status">Ce club n'a pas encore de membre.</p>
        } @else {
          <div class="table-scroll">
            <table>
              <thead>
                <tr>
                  <th scope="col">Nom</th>
                  <th scope="col">Prénom</th>
                  <th scope="col" class="numeric">Elo</th>
                  <th scope="col">Identifiant FFE</th>
                  <th scope="col">Licence</th>
                </tr>
              </thead>
              <tbody>
                @for (member of members; track member.ffeId) {
                  <tr>
                    <th scope="row">
                      {{ member.lastName }}
                      @if (member.fideTitle; as title) {
                        <abbr class="badge" [title]="titleName(title)">{{ title }}</abbr>
                      }
                    </th>
                    <td>{{ member.firstName }}</td>
                    <td class="numeric">
                      @if (member.elo !== null) {
                        {{ member.elo }}
                      } @else {
                        <abbr title="non classé">NC</abbr>
                      }
                    </td>
                    <td>{{ member.ffeId }}</td>
                    <td>{{ member.licenseType }}</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }
      }
      <form (submit)="register($event)">
        <fieldset>
          <legend>Inscrire un joueur licencié</legend>
          <div class="fields">
            <div class="field">
              <label for="first-name">Prénom</label>
              <input id="first-name" required autocomplete="off" [value]="draft().firstName"
                     (input)="update({ firstName: valueOf($event) })" />
            </div>
            <div class="field">
              <label for="last-name">Nom</label>
              <input id="last-name" required autocomplete="off" [value]="draft().lastName"
                     (input)="update({ lastName: valueOf($event) })" />
            </div>
            <div class="field">
              <label for="ffe-id">Identifiant FFE</label>
              <input id="ffe-id" required autocomplete="off" placeholder="ex. K58213" [value]="draft().ffeId"
                     (input)="update({ ffeId: valueOf($event) })" />
            </div>
            <div class="field">
              <label for="license-type">Licence</label>
              <select id="license-type" (change)="update({ licenseType: licenseOf($event) })">
                @for (type of licenseTypes; track type) {
                  <option [value]="type" [selected]="type === draft().licenseType">{{ type }}</option>
                }
              </select>
            </div>
            <div class="field">
              <label for="fide-title">Titre FIDE</label>
              <select id="fide-title" (change)="update({ fideTitle: titleOf($event) })">
                <option value="" [selected]="draft().fideTitle === ''">Aucun</option>
                @for (title of fideTitles; track title.code) {
                  <option [value]="title.code" [selected]="title.code === draft().fideTitle">
                    {{ title.code }} — {{ title.name }}
                  </option>
                }
              </select>
            </div>
            <div class="field">
              <label for="elo">Elo</label>
              <input id="elo" type="number" min="0" step="1" autocomplete="off" placeholder="facultatif"
                     [value]="draft().elo" (input)="update({ elo: valueOf($event) })" />
            </div>
          </div>
          <div class="actions">
            <button type="submit">Inscrire le joueur</button>
          </div>
        </fieldset>
        @if (outcome(); as outcome) {
          @if (outcome === 'refused') {
            <p class="notice notice--failure" role="alert">
              L'inscription n'a pas abouti : vérifiez les informations saisies.
            </p>
          } @else {
            <p class="notice notice--success" role="status">{{ outcome.registered }} est maintenant membre du club.</p>
          }
        }
      </form>
      <a routerLink="/">Retour aux clubs</a>
    </section>
  `,
})
export class MembersOfClub {
  private readonly clubMembers = inject(Members);
  private readonly clubId = inject(ActivatedRoute).snapshot.paramMap.get('id') ?? '';
  protected readonly licenseTypes = LICENSE_TYPES;
  protected readonly fideTitles = FIDE_TITLES;
  protected readonly shownMembers = signal<readonly Member[] | undefined>(undefined);
  protected readonly draft = signal<MemberDraft>(emptyDraft());
  protected readonly outcome = signal<{ registered: string } | 'refused' | undefined>(undefined);

  constructor() {
    this.consult();
  }

  protected update(typed: Partial<MemberDraft>): void {
    this.draft.update((draft) => ({ ...draft, ...typed }));
    this.outcome.set(undefined);
  }

  protected register(event: Event): void {
    event.preventDefault();
    const member = trimmed(this.draft());
    this.clubMembers.register(this.clubId, member).subscribe({
      next: () => {
        this.outcome.set({ registered: `${member.firstName} ${member.lastName}` });
        this.draft.set(emptyDraft());
        this.consult();
      },
      error: () => this.outcome.set('refused'),
    });
  }

  protected valueOf(event: Event): string {
    return (event.target as HTMLInputElement).value;
  }

  protected licenseOf(event: Event): LicenseType {
    return (event.target as HTMLSelectElement).value as LicenseType;
  }

  protected titleOf(event: Event): FideTitle | '' {
    return (event.target as HTMLSelectElement).value as FideTitle | '';
  }

  protected titleName(code: FideTitle): string {
    return FIDE_TITLES.find((title) => title.code === code)?.name ?? code;
  }

  private consult(): void {
    this.clubMembers.ofClub(this.clubId).subscribe((members) => this.shownMembers.set(members));
  }
}

function emptyDraft(): MemberDraft {
  return { firstName: '', lastName: '', ffeId: '', licenseType: 'A', elo: '', fideTitle: '' };
}

function trimmed(draft: MemberDraft): Member {
  return {
    firstName: draft.firstName.trim(),
    lastName: draft.lastName.trim(),
    ffeId: draft.ffeId.trim(),
    licenseType: draft.licenseType,
    elo: draft.elo.trim() === '' ? null : Number(draft.elo),
    fideTitle: draft.fideTitle === '' ? null : draft.fideTitle,
  };
}
