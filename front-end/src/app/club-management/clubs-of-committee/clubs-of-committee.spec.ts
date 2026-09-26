import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ClubsOfCommittee } from './clubs-of-committee';
import { Clubs } from '../ports/clubs';
import { HttpClubs } from '../http/http-clubs';

describe('ClubsOfCommittee', () => {
  let fixture: ComponentFixture<ClubsOfCommittee>;
  let server: HttpTestingController;
  let page: HTMLElement;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Clubs, useClass: HttpClubs },
      ],
    });
    fixture = TestBed.createComponent(ClubsOfCommittee);
    server = TestBed.inject(HttpTestingController);
    page = fixture.nativeElement as HTMLElement;
    await fixture.whenStable();
  });

  async function consultClubsOfCommittee(committeeCode: string, clubs: unknown[]): Promise<void> {
    fill(fieldLabelled(page, 'Numéro de votre département'), committeeCode);
    buttonNamed(page, 'Afficher les clubs').click();
    server
      .expectOne(
        (request) => request.url === '/clubs' && request.params.get('committee') === committeeCode,
      )
      .flush(clubs);
    await fixture.whenStable();
  }

  it('shows the administrator the clubs of the committee they ask for', async () => {
    await consultClubsOfCommittee('45', [
      { name: 'U.S. Orléans.Echecs', commune: 'Orléans', ffeClubId: 'G45001' },
    ]);

    expect(shownClubs(page)).toEqual([['U.S. Orléans.Echecs', 'Orléans', 'G45001']]);
  });

  it('tells the administrator when no club is managed by the application in the committee', async () => {
    await consultClubsOfCommittee('45', []);

    expect(page.textContent).toContain("Aucun club n'est géré par l'application dans ce comité.");
  });
});

function shownClubs(page: HTMLElement): string[][] {
  return Array.from(page.querySelectorAll('tbody tr')).map((row) =>
    Array.from(row.querySelectorAll('th, td')).map((cell) => cell.textContent?.trim() ?? ''),
  );
}

function fill(field: HTMLInputElement, value: string): void {
  field.value = value;
  field.dispatchEvent(new Event('input'));
}

function fieldLabelled(page: HTMLElement, text: string): HTMLInputElement {
  const label = Array.from(page.querySelectorAll('label')).find(
    (l) => l.textContent?.trim() === text,
  );
  if (!label?.control) throw new Error(`No field labelled "${text}"`);
  return label.control as HTMLInputElement;
}

function buttonNamed(page: HTMLElement, text: string): HTMLButtonElement {
  const button = Array.from(page.querySelectorAll('button')).find(
    (b) => b.textContent?.trim() === text,
  );
  if (!button) throw new Error(`No button named "${text}"`);
  return button;
}
