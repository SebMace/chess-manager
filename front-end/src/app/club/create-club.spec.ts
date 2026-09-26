import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CreateClub } from './create-club';
import { Clubs } from './clubs';
import { HttpClubs } from './http-clubs';

describe('CreateClub', () => {
  let fixture: ComponentFixture<CreateClub>;
  let server: HttpTestingController;
  let page: HTMLElement;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), { provide: Clubs, useClass: HttpClubs }],
    });
    fixture = TestBed.createComponent(CreateClub);
    server = TestBed.inject(HttpTestingController);
    page = fixture.nativeElement as HTMLElement;
    await fixture.whenStable();
  });

  it('tells the administrator that the club has been created with its information', async () => {
    createClub(page, {
      'Nom du club': 'U.S. Orléans.Echecs',
      'Code du comité': '45',
      'Identifiant FFE': 'G45001',
      'Commune': 'Orléans',
    });

    const request = server.expectOne({ method: 'POST', url: '/clubs' });
    expect(request.request.body).toEqual(
      { name: 'U.S. Orléans.Echecs', committeeCode: '45', ffeClubId: 'G45001', commune: 'Orléans' });
    request.flush(null, { status: 201, statusText: 'Created', headers: { Location: '/clubs/7' } });
    await fixture.whenStable();

    expect(page.textContent).toContain('Le club U.S. Orléans.Echecs a été créé.');
  });

  it('tells the administrator that the club could not be created', async () => {
    createValidClub(page, 'Montargis');

    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 500, statusText: 'Internal Server Error' });
    await fixture.whenStable();

    expect(page.textContent).not.toContain('Le club Montargis a été créé.');
    expect(page.textContent).toContain("Le club n'a pas pu être créé. Réessayez.");
  });

  it('no longer tells the administrator that the club could not be created once it has been created', async () => {
    createValidClub(page, 'Montargis');
    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 500, statusText: 'Internal Server Error' });
    await fixture.whenStable();

    createValidClub(page, 'Montargis');
    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 201, statusText: 'Created', headers: { Location: '/clubs/6' } });
    await fixture.whenStable();

    expect(page.textContent).toContain('Le club Montargis a été créé.');
    expect(page.textContent).not.toContain("Le club n'a pas pu être créé. Réessayez.");
  });

  it('no longer tells the administrator that a previous club has been created once a creation fails', async () => {
    createValidClub(page, 'Montargis');
    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 201, statusText: 'Created', headers: { Location: '/clubs/6' } });
    await fixture.whenStable();

    createValidClub(page, 'Olivet');
    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 500, statusText: 'Internal Server Error' });
    await fixture.whenStable();

    expect(page.textContent).toContain("Le club n'a pas pu être créé. Réessayez.");
    expect(page.textContent).not.toContain('Le club Montargis a été créé.');
  });

  it('tells the administrator that the departmental committee is required', async () => {
    createClubWithoutCommittee(page, 'Montargis');
    await fixture.whenStable();

    server.expectNone({ method: 'POST', url: '/clubs' });
    expect(page.textContent).toContain('Le code du comité est obligatoire.');
  });

  it('no longer tells the administrator that the committee is required once it is given', async () => {
    createClubWithoutCommittee(page, 'Montargis');
    await fixture.whenStable();

    createValidClub(page, 'Montargis');
    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 201, statusText: 'Created', headers: { Location: '/clubs/6' } });
    await fixture.whenStable();

    expect(page.textContent).toContain('Le club Montargis a été créé.');
    expect(page.textContent).not.toContain('Le code du comité est obligatoire.');
  });

  it('tells the administrator that the FFE identifier is required', async () => {
    createClub(page, { 'Nom du club': 'U.S. Orléans.Echecs', 'Code du comité': '45' });
    await fixture.whenStable();

    server.expectNone({ method: 'POST', url: '/clubs' });
    expect(page.textContent).toContain("L'identifiant FFE est obligatoire.");
  });
});

function createClubWithoutCommittee(page: HTMLElement, name: string): void {
  createClub(page, { 'Nom du club': name });
}

function createClub(page: HTMLElement, fields: Record<string, string>): void {
  for (const [label, value] of Object.entries(fields)) fill(fieldLabelled(page, label), value);
  buttonNamed(page, 'Créer le club').click();
}

function createValidClub(page: HTMLElement, name: string): void {
  createClub(page, { 'Nom du club': name, 'Code du comité': '45', 'Identifiant FFE': 'G45001' });
}

function fill(field: HTMLInputElement, value: string): void {
  field.value = value;
  field.dispatchEvent(new Event('input'));
}

function fieldLabelled(page: HTMLElement, text: string): HTMLInputElement {
  const label = Array.from(page.querySelectorAll('label')).find(l => l.textContent?.trim() === text);
  if (!label?.control) throw new Error(`No field labelled "${text}"`);
  return label.control as HTMLInputElement;
}

function buttonNamed(page: HTMLElement, text: string): HTMLButtonElement {
  const button = Array.from(page.querySelectorAll('button')).find(b => b.textContent?.trim() === text);
  if (!button) throw new Error(`No button named "${text}"`);
  return button;
}
