import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, TestRequest, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { MembersOfClub } from './members-of-club';
import { Members } from '../ports/members';
import { HttpMembers } from '../http/http-members';

const MEMBERS = '/api/clubs/club-1/members';

const CAMILLE = {
  firstName: 'Camille',
  lastName: 'Martin',
  ffeId: 'K58213',
  licenseType: 'A',
  elo: 1850,
  fideTitle: null,
};
const ALEX_WITHOUT_RATING = {
  firstName: 'Alex',
  lastName: 'Durand',
  ffeId: 'K61477',
  licenseType: 'B',
  elo: null,
  fideTitle: null,
};
const MAXIME_TITLED = {
  firstName: 'Maxime',
  lastName: 'Roux',
  ffeId: 'K70001',
  licenseType: 'A',
  elo: 2310,
  fideTitle: 'MF',
};

describe('MembersOfClub', () => {
  let fixture: ComponentFixture<MembersOfClub>;
  let server: HttpTestingController;
  let page: HTMLElement;

  async function openMembers(members: unknown[]): Promise<void> {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({ id: 'club-1' }) } } },
        { provide: Members, useClass: HttpMembers },
      ],
    });
    fixture = TestBed.createComponent(MembersOfClub);
    server = TestBed.inject(HttpTestingController);
    page = fixture.nativeElement as HTMLElement;
    await fixture.whenStable();
    server.expectOne({ method: 'GET', url: MEMBERS }).flush(members);
    await fixture.whenStable();
  }

  async function enterCamille(): Promise<void> {
    fill(fieldLabelled(page, 'Prénom'), 'Camille');
    fill(fieldLabelled(page, 'Nom'), 'Martin');
    fill(fieldLabelled(page, 'Identifiant FFE'), 'K58213');
    choose(fieldLabelled(page, 'Licence') as unknown as HTMLSelectElement, 'A');
    fill(fieldLabelled(page, 'Elo'), '1850');
    await fixture.whenStable();
  }

  function register(): TestRequest {
    buttonNamed(page, 'Inscrire le joueur').click();
    return server.expectOne({ method: 'POST', url: MEMBERS });
  }

  it('shows the administrator the members of the club with their rating', async () => {
    await openMembers([CAMILLE]);

    expect(shownMembers(page)).toEqual([['Martin', 'Camille', '1850', 'K58213', 'A']]);
  });

  it('shows the FIDE title of a member as a badge next to their name, without a column of its own', async () => {
    await openMembers([MAXIME_TITLED, CAMILLE]);

    const badge = page.querySelector('tbody tr:first-child th .badge');
    expect(badge?.textContent?.trim()).toBe('MF');
    expect(badge?.getAttribute('title')).toBe('Maître FIDE');
    expect(page.querySelectorAll('tbody tr:first-child th, tbody tr:first-child td').length).toBe(5);
    expect(page.querySelectorAll('tbody tr:last-child .badge').length).toBe(0);
  });

  it('registers a member with their FIDE title', async () => {
    await openMembers([]);
    fill(fieldLabelled(page, 'Prénom'), 'Maxime');
    fill(fieldLabelled(page, 'Nom'), 'Roux');
    fill(fieldLabelled(page, 'Identifiant FFE'), 'K70001');
    fill(fieldLabelled(page, 'Elo'), '2310');
    choose(fieldLabelled(page, 'Titre FIDE') as unknown as HTMLSelectElement, 'MF');
    await fixture.whenStable();

    expect(register().request.body).toEqual(MAXIME_TITLED);
  });

  it('shows NC for a member who is not rated', async () => {
    await openMembers([ALEX_WITHOUT_RATING]);

    expect(shownMembers(page)).toEqual([['Durand', 'Alex', 'NC', 'K61477', 'B']]);
  });

  it('tells the administrator when the club has no member yet', async () => {
    await openMembers([]);

    expect(page.textContent).toContain("Ce club n'a pas encore de membre.");
  });

  it('registers a licensed player as a member, tells the administrator so and shows them among the members', async () => {
    await openMembers([]);
    await enterCamille();

    const request = register();
    request.flush(null, { status: 201, statusText: 'Created' });
    server.expectOne({ method: 'GET', url: MEMBERS }).flush([CAMILLE]);
    await fixture.whenStable();

    expect(request.request.body).toEqual(CAMILLE);
    expect(page.textContent).toContain('Camille Martin est maintenant membre du club.');
    expect(shownMembers(page)).toEqual([['Martin', 'Camille', '1850', 'K58213', 'A']]);
    expect(fieldLabelled(page, 'Prénom').value).toBe('');
    expect(fieldLabelled(page, 'Elo').value).toBe('');
  });

  it('registers a member who is not rated, without a rating', async () => {
    await openMembers([]);
    fill(fieldLabelled(page, 'Prénom'), 'Alex');
    fill(fieldLabelled(page, 'Nom'), 'Durand');
    fill(fieldLabelled(page, 'Identifiant FFE'), 'K61477');
    choose(fieldLabelled(page, 'Licence') as unknown as HTMLSelectElement, 'B');
    await fixture.whenStable();

    const request = register();

    expect(request.request.body).toEqual(ALEX_WITHOUT_RATING);
  });

  it('tells the administrator when the registration did not succeed, without changing the members shown', async () => {
    await openMembers([CAMILLE]);
    await enterCamille();

    register().flush(null, { status: 500, statusText: 'Server Error' });
    await fixture.whenStable();

    expect(page.textContent).toContain("L'inscription n'a pas abouti");
    expect(shownMembers(page)).toEqual([['Martin', 'Camille', '1850', 'K58213', 'A']]);
    server.verify();
  });
});

function shownMembers(page: HTMLElement): string[][] {
  return Array.from(page.querySelectorAll('tbody tr')).map((row) =>
    Array.from(row.querySelectorAll('th, td')).map((cell) => cell.textContent?.trim() ?? ''),
  );
}

function fill(field: HTMLInputElement, value: string): void {
  field.value = value;
  field.dispatchEvent(new Event('input'));
}

function choose(field: HTMLSelectElement, value: string): void {
  field.value = value;
  field.dispatchEvent(new Event('change'));
}

function fieldLabelled(page: HTMLElement, text: string): HTMLInputElement {
  const label = Array.from(page.querySelectorAll('label')).find((l) => l.textContent?.trim() === text);
  if (!label?.control) throw new Error(`No field labelled "${text}"`);
  return label.control as HTMLInputElement;
}

function buttonNamed(page: HTMLElement, text: string): HTMLButtonElement {
  const button = Array.from(page.querySelectorAll('button')).find((b) => b.textContent?.trim() === text);
  if (!button) throw new Error(`No button named "${text}"`);
  return button;
}
