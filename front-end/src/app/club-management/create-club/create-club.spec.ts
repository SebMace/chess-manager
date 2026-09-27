import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CreateClub } from './create-club';
import { Clubs } from '../ports/clubs';
import { HttpClubs } from '../http/http-clubs';
import { Commune, Communes } from '../ports/communes';
import { HttpCommunes } from '../http/http-communes';
import { Towns } from '../ports/towns';
import { HttpTowns } from '../http/http-towns';

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
        { provide: Towns, useClass: HttpTowns },
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
    fillFields(page, { 'Nom du club': name, 'Code du comité': '45', 'Identifiant FFE': 'G45001' });
    await chooseCommune('Orl', 'Orléans');
    fillFields(page, REGISTERED_OFFICE);
    playsAtRegisteredOffice(page);
    buttonNamed(page, 'Créer le club').click();
  }

  it('tells the administrator that the club has been created with its information', async () => {
    fill(fieldLabelled(page, 'Nom du club'), 'U.S. Orléans.Echecs');
    fill(fieldLabelled(page, 'Code du comité'), '45');
    fill(fieldLabelled(page, 'Identifiant FFE'), 'G45001');
    await chooseCommune('Orl', 'Orléans');
    fillFields(page, REGISTERED_OFFICE);
    fill(fieldInGroup(page, 'Salle de jeu', 'Numéro et voie'), '5 rue du Roi');
    fill(fieldInGroup(page, 'Salle de jeu', 'Code postal'), '45100');
    fill(fieldInGroup(page, 'Salle de jeu', 'Localité'), 'Orléans');
    buttonNamed(page, 'Créer le club').click();

    const request = server.expectOne({ method: 'POST', url: '/clubs' });
    expect(request.request.body).toEqual({
      name: 'U.S. Orléans.Echecs',
      committeeCode: '45',
      ffeClubId: 'G45001',
      communeCode: '45234',
      registeredOffice: { street: '12 rue des Échecs', postcode: '45000', town: 'Orléans' },
      playingVenue: { street: '5 rue du Roi', postcode: '45100', town: 'Orléans' },
    });
    request.flush(null, { status: 201, statusText: 'Created', headers: { Location: '/clubs/7' } });
    await fixture.whenStable();

    expect(page.textContent).toContain('Le club U.S. Orléans.Echecs a été créé.');
  });

  it('lets the administrator say that the club plays at its registered office', async () => {
    fillFields(page, { 'Nom du club': 'U.S. Orléans.Echecs', 'Code du comité': '45', 'Identifiant FFE': 'G45001', ...REGISTERED_OFFICE });
    await chooseCommune('Orl', 'Orléans');
    playsAtRegisteredOffice(page);
    await fixture.whenStable();

    expect(() => fieldInGroup(page, 'Salle de jeu', 'Numéro et voie')).toThrow();
    buttonNamed(page, 'Créer le club').click();

    expect(server.expectOne({ method: 'POST', url: '/clubs' }).request.body.playingVenue)
      .toEqual({ street: '12 rue des Échecs', postcode: '45000', town: 'Orléans' });
  });

  it('proposes the town of the chosen commune for the registered office and the playing venue', async () => {
    fill(fieldLabelled(page, 'Code du comité'), '45');
    await chooseCommune('Orl', 'Orléans');

    server.expectOne(request => request.method === 'GET' && request.url === '/towns'
      && request.params.get('commune') === '45234').flush([{ name: 'ORLEANS', postcodes: ['45000', '45100'] }]);
    await fixture.whenStable();

    expect(fieldInGroup(page, 'Siège social', 'Localité').value).toBe('ORLEANS');
    expect(fieldInGroup(page, 'Salle de jeu', 'Localité').value).toBe('ORLEANS');
  });

  async function flushTownsOfCommune(communeCode: string, towns: { name: string; postcodes: string[] }[]): Promise<void> {
    server.expectOne(request => request.url === '/towns' && request.params.get('commune') === communeCode).flush(towns);
    await fixture.whenStable();
  }

  it('replaces the postcode chosen for the previous commune when another commune is chosen', async () => {
    fill(fieldLabelled(page, 'Code du comité'), '45');
    await chooseCommune('Orl', 'Orléans');
    await flushTownsOfCommune('45234', [{ name: 'ORLEANS', postcodes: ['45000', '45100'] }]);
    fill(fieldInGroup(page, 'Siège social', 'Code postal'), '45000');
    for (const request of server.match(request => request.url === '/towns')) request.flush(['ORLEANS']);
    await fixture.whenStable();

    await chooseCommune('Oli', 'Olivet');
    await flushTownsOfCommune('45232', [{ name: 'OLIVET', postcodes: ['45160'] }]);

    expect(fieldInGroup(page, 'Siège social', 'Code postal').value).toBe('45160');
    expect(fieldInGroup(page, 'Siège social', 'Localité').value).toBe('OLIVET');
  });

  it('forgets the town and the postcode of the addresses as soon as the commune is changed', async () => {
    fill(fieldLabelled(page, 'Code du comité'), '45');
    await chooseCommune('Oli', 'Olivet');
    await flushTownsOfCommune('45232', [{ name: 'OLIVET', postcodes: ['45160'] }]);

    fill(fieldLabelled(page, 'Commune'), 'Orl');
    await fixture.whenStable();

    for (const group of ['Siège social', 'Salle de jeu']) {
      expect(fieldInGroup(page, group, 'Localité').value).toBe('');
      expect(fieldInGroup(page, group, 'Code postal').value).toBe('');
    }
  });

  it('proposes no town when the commune is no longer chosen by the time its towns are found', async () => {
    fill(fieldLabelled(page, 'Code du comité'), '45');
    await chooseCommune('Orl', 'Orléans');

    fill(fieldLabelled(page, 'Commune'), 'Ol');
    await flushTownsOfCommune('45234', [{ name: 'ORLEANS', postcodes: ['45000', '45100'] }]);

    expect(fieldInGroup(page, 'Siège social', 'Localité').value).toBe('');
    expect(fieldInGroup(page, 'Salle de jeu', 'Localité').value).toBe('');
  });

  it('proposes the town of the latest commune chosen when the towns of a previous one arrive late', async () => {
    fill(fieldLabelled(page, 'Code du comité'), '45');
    await chooseCommune('Orl', 'Orléans');
    await chooseCommune('Oli', 'Olivet');

    await flushTownsOfCommune('45232', [{ name: 'OLIVET', postcodes: ['45160'] }]);
    await flushTownsOfCommune('45234', [{ name: 'ORLEANS', postcodes: ['45000', '45100'] }]);

    expect(fieldInGroup(page, 'Siège social', 'Localité').value).toBe('OLIVET');
    expect(fieldInGroup(page, 'Siège social', 'Code postal').value).toBe('45160');
  });

  it('keeps proposing the town of the latest commune chosen when the search for a previous one fails', async () => {
    fill(fieldLabelled(page, 'Code du comité'), '45');
    playsAtRegisteredOffice(page);
    await chooseCommune('Orl', 'Orléans');
    await chooseCommune('Oli', 'Olivet');
    await flushTownsOfCommune('45232', [{ name: 'OLIVET', postcodes: ['45160'] }]);
    server.expectOne(request => request.url === '/towns' && request.params.get('commune') === '45234')
      .flush(null, { status: 500, statusText: 'Internal Server Error' });

    fieldLabelled(page, 'La salle de jeu est au siège social').click();
    await fixture.whenStable();

    expect(fieldInGroup(page, 'Salle de jeu', 'Localité').value).toBe('OLIVET');
  });

  it('gives up the towns of the postcode typed for the previous commune once the commune is changed', async () => {
    fill(fieldLabelled(page, 'Code du comité'), '45');
    await chooseCommune('Orl', 'Orléans');
    await flushTownsOfCommune('45234', []);
    fill(fieldInGroup(page, 'Siège social', 'Code postal'), '45100');

    fill(fieldLabelled(page, 'Commune'), 'Ol');
    await fixture.whenStable();
    server.expectOne(request => request.url === '/towns' && request.params.get('postcode') === '45100').flush(['ORLEANS']);
    await fixture.whenStable();

    expect(fieldInGroup(page, 'Siège social', 'Localité').value).toBe('');
  });

  it('gives up the towns of the postcode typed for the previous commune even when they arrive before the screen is refreshed', async () => {
    fill(fieldLabelled(page, 'Code du comité'), '45');
    await chooseCommune('Orl', 'Orléans');
    await flushTownsOfCommune('45234', []);
    fill(fieldInGroup(page, 'Siège social', 'Code postal'), '45100');
    await fixture.whenStable();

    fill(fieldLabelled(page, 'Commune'), 'Ol');
    server.expectOne(request => request.url === '/towns' && request.params.get('postcode') === '45100').flush(['ORLEANS']);
    await fixture.whenStable();

    expect(fieldInGroup(page, 'Siège social', 'Code postal').value).toBe('');
    expect(fieldInGroup(page, 'Siège social', 'Localité').value).toBe('');
  });

  it('no longer suggests the postcodes of the town of the commune once the commune is changed', async () => {
    fill(fieldLabelled(page, 'Code du comité'), '45');
    await chooseCommune('Orl', 'Orléans');
    await flushTownsOfCommune('45234', [{ name: 'ORLEANS', postcodes: ['45000', '45100'] }]);

    fill(fieldLabelled(page, 'Commune'), 'Ol');
    await fixture.whenStable();

    expect(suggestionsFor(page, fieldInGroup(page, 'Siège social', 'Code postal'))).toEqual([]);
  });

  it('no longer suggests the towns of the postcode typed for the previous commune once the commune is changed', async () => {
    fill(fieldLabelled(page, 'Code du comité'), '45');
    await chooseCommune('Orl', 'Orléans');
    await flushTownsOfCommune('45234', []);
    fill(fieldInGroup(page, 'Siège social', 'Code postal'), '45240');
    server.expectOne(request => request.url === '/towns' && request.params.get('postcode') === '45240')
      .flush(['LA FERTE ST AUBIN', 'LIGNY LE RIBAULT', 'SENNELY']);
    await fixture.whenStable();

    fill(fieldLabelled(page, 'Commune'), 'Ol');
    await fixture.whenStable();

    expect(suggestionsFor(page, fieldInGroup(page, 'Siège social', 'Localité'))).toEqual([]);
  });

  it('stops looking for the towns of the chosen commune once the form is no longer shown', async () => {
    fill(fieldLabelled(page, 'Code du comité'), '45');
    await chooseCommune('Orl', 'Orléans');

    fixture.destroy();

    expect(server.expectOne(request => request.url === '/towns' && request.params.get('commune') === '45234').cancelled)
      .toBe(true);
  });

  it('proposes no town when the towns of the chosen commune cannot be found', async () => {
    fill(fieldLabelled(page, 'Code du comité'), '45');
    await chooseCommune('Orl', 'Orléans');

    server.expectOne(request => request.url === '/towns' && request.params.has('commune'))
      .flush(null, { status: 500, statusText: 'Internal Server Error' });
    await fixture.whenStable();

    expect(fieldInGroup(page, 'Siège social', 'Localité').value).toBe('');
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
    playsAtRegisteredOffice(page);
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

  it('tells the administrator that each part of the playing venue is required unless it is the registered office', async () => {
    fillFields(page, { 'Nom du club': 'U.S. Orléans.Echecs', 'Code du comité': '45', 'Identifiant FFE': 'G45001', ...REGISTERED_OFFICE });
    await chooseCommune('Orl', 'Orléans');
    buttonNamed(page, 'Créer le club').click();
    await fixture.whenStable();

    server.expectNone({ method: 'POST', url: '/clubs' });
    const venue = groupNamed(page, 'Salle de jeu').textContent;
    expect(venue).toContain('Le numéro et la voie sont obligatoires.');
    expect(venue).toContain('Le code postal est obligatoire.');
    expect(venue).toContain('La localité est obligatoire.');
  });

  it('tells the administrator that a postcode is made of five digits', async () => {
    fillFields(page, { 'Nom du club': 'U.S. Orléans.Echecs', 'Code du comité': '45', 'Identifiant FFE': 'G45001' });
    await chooseCommune('Orl', 'Orléans');
    fillFields(page, { ...REGISTERED_OFFICE, 'Code postal': '4500' });
    buttonNamed(page, 'Créer le club').click();
    await fixture.whenStable();

    server.expectNone({ method: 'POST', url: '/clubs' });
    expect(page.textContent).toContain('Le code postal doit comporter 5 chiffres.');
  });

  it('tells the administrator that the postcode of the playing venue is made of five digits', async () => {
    fillFields(page, { 'Nom du club': 'U.S. Orléans.Echecs', 'Code du comité': '45', 'Identifiant FFE': 'G45001', ...REGISTERED_OFFICE });
    await chooseCommune('Orl', 'Orléans');
    fill(fieldInGroup(page, 'Salle de jeu', 'Numéro et voie'), '5 rue du Roi');
    fill(fieldInGroup(page, 'Salle de jeu', 'Code postal'), '4510');
    fill(fieldInGroup(page, 'Salle de jeu', 'Localité'), 'Orléans');
    buttonNamed(page, 'Créer le club').click();
    await fixture.whenStable();

    server.expectNone({ method: 'POST', url: '/clubs' });
    expect(groupNamed(page, 'Salle de jeu').textContent).toContain('Le code postal doit comporter 5 chiffres.');
  });

  it('accepts a postcode typed with a space', async () => {
    fillFields(page, { 'Nom du club': 'U.S. Orléans.Echecs', 'Code du comité': '45', 'Identifiant FFE': 'G45001' });
    await chooseCommune('Orl', 'Orléans');
    fillFields(page, { ...REGISTERED_OFFICE, 'Code postal': '45 000' });
    playsAtRegisteredOffice(page);
    buttonNamed(page, 'Créer le club').click();

    server.expectOne({ method: 'POST', url: '/clubs' });
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

function playsAtRegisteredOffice(page: HTMLElement): void {
  const atRegisteredOffice = fieldLabelled(page, 'La salle de jeu est au siège social');
  if (!atRegisteredOffice.checked) atRegisteredOffice.click();
}

function suggestionsFor(page: HTMLElement, field: HTMLInputElement): string[] {
  const list = field.getAttribute('list');
  if (!list) return [];
  return Array.from(page.querySelectorAll<HTMLOptionElement>(`datalist[id="${list}"] option`)).map(option => option.value);
}

function fieldInGroup(page: HTMLElement, group: string, text: string): HTMLInputElement {
  return fieldLabelled(groupNamed(page, group), text);
}

function groupNamed(page: HTMLElement, group: string): HTMLFieldSetElement {
  const fieldset = Array.from(page.querySelectorAll('fieldset')).find(f => f.querySelector('legend')?.textContent?.trim() === group);
  if (!fieldset) throw new Error(`No group named "${group}"`);
  return fieldset;
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
