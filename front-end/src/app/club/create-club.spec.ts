import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CreateClub } from './create-club';
import { Clubs } from './clubs';
import { HttpClubs } from './http-clubs';
import { Commune, Communes } from '../commune/communes';
import { HttpCommunes } from '../commune/http-communes';

const REGISTERED_OFFICE = { 'Numéro et voie': '12 rue des Échecs', 'Code postal': '45000', 'Localité': 'Orléans' };

const LOIRET: Commune[] = [
  { code: '45234', name: 'Orléans' },
  { code: '45232', name: 'Olivet' },
  { code: '45298', name: 'Saint-Pryvé-Saint-Mesmin' },
  { code: '45188', name: 'Loury' },
];

describe('CreateClub', () => {
  let fixture: ComponentFixture<CreateClub>;
  let server: HttpTestingController;
  let page: HTMLElement;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Clubs, useClass: HttpClubs },
        { provide: Communes, useClass: HttpCommunes },
      ],
    });
    fixture = TestBed.createComponent(CreateClub);
    server = TestBed.inject(HttpTestingController);
    page = fixture.nativeElement as HTMLElement;
    await fixture.whenStable();
  });

  async function chooseCommune(typed: string, name: string): Promise<void> {
    fill(fieldLabelled(page, 'Commune'), typed);
    for (const request of server.match(request => request.url === '/communes')) request.flush(LOIRET);
    await fixture.whenStable();
    optionNamed(page, name).click();
    await fixture.whenStable();
  }

  async function createValidClub(name: string): Promise<void> {
    fillFields(page, { 'Nom du club': name, 'Code du comité': '45', 'Identifiant FFE': 'G45001', ...REGISTERED_OFFICE });
    await chooseCommune('Orl', 'Orléans');
    buttonNamed(page, 'Créer le club').click();
  }

  it('tells the administrator that the club has been created with its information', async () => {
    fill(fieldLabelled(page, 'Nom du club'), 'U.S. Orléans.Echecs');
    fill(fieldLabelled(page, 'Code du comité'), '45');
    fill(fieldLabelled(page, 'Identifiant FFE'), 'G45001');
    await chooseCommune('Orl', 'Orléans');
    fillFields(page, REGISTERED_OFFICE);
    buttonNamed(page, 'Créer le club').click();

    const request = server.expectOne({ method: 'POST', url: '/clubs' });
    expect(request.request.body).toEqual({
      name: 'U.S. Orléans.Echecs',
      committeeCode: '45',
      ffeClubId: 'G45001',
      communeCode: '45234',
      registeredOffice: { street: '12 rue des Échecs', postcode: '45000', town: 'Orléans' },
    });
    request.flush(null, { status: 201, statusText: 'Created', headers: { Location: '/clubs/7' } });
    await fixture.whenStable();

    expect(page.textContent).toContain('Le club U.S. Orléans.Echecs a été créé.');
  });

  it('offers the communes of the department of the committee that start with the letters typed', async () => {
    fill(fieldLabelled(page, 'Code du comité'), '45');
    fill(fieldLabelled(page, 'Commune'), 'Ol');

    server.expectOne(request => request.method === 'GET' && request.url === '/communes'
      && request.params.get('committee') === '45').flush(LOIRET);
    await fixture.whenStable();

    expect(offeredCommunes(page)).toEqual(['Olivet']);
  });

  it('lets the administrator choose a commune with the keyboard', async () => {
    fillFields(page, { 'Nom du club': 'U.S. Orléans.Echecs', 'Code du comité': '45', 'Identifiant FFE': 'G45001', ...REGISTERED_OFFICE });
    const commune = fieldLabelled(page, 'Commune');
    fill(commune, 'O');
    server.expectOne(request => request.url === '/communes').flush(LOIRET);
    await fixture.whenStable();

    press(commune, 'ArrowDown');
    press(commune, 'ArrowDown');
    await fixture.whenStable();
    expect(commune.getAttribute('aria-activedescendant')).toBe(optionNamed(page, 'Orléans').id);

    press(commune, 'Enter');
    await fixture.whenStable();
    buttonNamed(page, 'Créer le club').click();

    expect(server.expectOne({ method: 'POST', url: '/clubs' }).request.body.communeCode).toBe('45234');
  });

  it('lets the administrator go back up the offered communes and close them', async () => {
    fill(fieldLabelled(page, 'Code du comité'), '45');
    const commune = fieldLabelled(page, 'Commune');
    fill(commune, 'O');
    server.expectOne(request => request.url === '/communes').flush(LOIRET);
    await fixture.whenStable();

    press(commune, 'ArrowDown');
    press(commune, 'ArrowDown');
    press(commune, 'ArrowUp');
    await fixture.whenStable();
    expect(commune.getAttribute('aria-activedescendant')).toBe(optionNamed(page, 'Olivet').id);

    press(commune, 'Escape');
    await fixture.whenStable();
    expect(offeredCommunes(page)).toEqual([]);
    expect(commune.getAttribute('aria-expanded')).toBe('false');
  });

  it('tells the administrator that the club could not be created', async () => {
    await createValidClub('Montargis');

    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 500, statusText: 'Internal Server Error' });
    await fixture.whenStable();

    expect(page.textContent).not.toContain('Le club Montargis a été créé.');
    expect(page.textContent).toContain("Le club n'a pas pu être créé. Réessayez.");
  });

  it('no longer tells the administrator that the club could not be created once it has been created', async () => {
    await createValidClub('Montargis');
    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 500, statusText: 'Internal Server Error' });
    await fixture.whenStable();

    await createValidClub('Montargis');
    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 201, statusText: 'Created', headers: { Location: '/clubs/6' } });
    await fixture.whenStable();

    expect(page.textContent).toContain('Le club Montargis a été créé.');
    expect(page.textContent).not.toContain("Le club n'a pas pu être créé. Réessayez.");
  });

  it('no longer tells the administrator that a previous club has been created once a creation fails', async () => {
    await createValidClub('Montargis');
    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 201, statusText: 'Created', headers: { Location: '/clubs/6' } });
    await fixture.whenStable();

    await createValidClub('Olivet');
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

    await createValidClub('Montargis');
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

  it('tells the administrator that the commune is required', async () => {
    createClub(page, { 'Nom du club': 'U.S. Orléans.Echecs', 'Code du comité': '45', 'Identifiant FFE': 'G45001' });
    await fixture.whenStable();

    server.expectNone({ method: 'POST', url: '/clubs' });
    expect(page.textContent).toContain('La commune est obligatoire.');
  });

  it('tells the administrator that each part of the registered office is required', async () => {
    fillFields(page, { 'Nom du club': 'U.S. Orléans.Echecs', 'Code du comité': '45', 'Identifiant FFE': 'G45001' });
    await chooseCommune('Orl', 'Orléans');
    buttonNamed(page, 'Créer le club').click();
    await fixture.whenStable();

    server.expectNone({ method: 'POST', url: '/clubs' });
    expect(page.textContent).toContain('Le numéro et la voie sont obligatoires.');
    expect(page.textContent).toContain('Le code postal est obligatoire.');
    expect(page.textContent).toContain('La localité est obligatoire.');
  });

  it('tells the administrator to choose the commune among those offered', async () => {
    fillFields(page, { 'Nom du club': 'U.S. Orléans.Echecs', 'Code du comité': '45', 'Identifiant FFE': 'G45001' });
    fill(fieldLabelled(page, 'Commune'), 'Orl');
    server.expectOne(request => request.url === '/communes').flush(LOIRET);
    buttonNamed(page, 'Créer le club').click();
    await fixture.whenStable();

    server.expectNone({ method: 'POST', url: '/clubs' });
    expect(page.textContent).toContain('Choisissez la commune parmi celles proposées.');
    expect(page.textContent).not.toContain('La commune est obligatoire.');
  });

  it('tells the administrator that the FFE identifier is already used by another club', async () => {
    await createValidClub('Échiquier Orléanais');

    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 409, statusText: 'Conflict' });
    await fixture.whenStable();

    expect(page.textContent).toContain('Un club avec l\'identifiant FFE G45001 existe déjà.');
    expect(page.textContent).not.toContain("Le club n'a pas pu être créé. Réessayez.");
  });
});

function createClubWithoutCommittee(page: HTMLElement, name: string): void {
  createClub(page, { 'Nom du club': name });
}

function createClub(page: HTMLElement, fields: Record<string, string>): void {
  fillFields(page, fields);
  buttonNamed(page, 'Créer le club').click();
}

function fillFields(page: HTMLElement, fields: Record<string, string>): void {
  for (const [label, value] of Object.entries(fields)) fill(fieldLabelled(page, label), value);
}


function optionNamed(page: HTMLElement, name: string): HTMLElement {
  const option = Array.from(page.querySelectorAll<HTMLElement>('[role="option"]')).find(o => o.textContent?.trim() === name);
  if (!option) throw new Error(`No commune offered named "${name}"`);
  return option;
}

function offeredCommunes(page: HTMLElement): string[] {
  return Array.from(page.querySelectorAll('[role="option"]')).map(option => option.textContent?.trim() ?? '');
}

function press(field: HTMLInputElement, key: string): void {
  field.dispatchEvent(new KeyboardEvent('keydown', { key, bubbles: true, cancelable: true }));
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
