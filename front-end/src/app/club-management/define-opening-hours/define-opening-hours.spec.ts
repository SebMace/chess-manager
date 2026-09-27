import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, TestRequest, provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { DefineOpeningHours } from './define-opening-hours';
import { OpeningHours } from '../ports/opening-hours';
import { HttpOpeningHours } from '../http/http-opening-hours';

const OPENING_HOURS = '/clubs/club-1/opening-hours';

describe('DefineOpeningHours', () => {
  let fixture: ComponentFixture<DefineOpeningHours>;
  let server: HttpTestingController;
  let page: HTMLElement;

  async function openOpeningHours(sessions: unknown[]): Promise<void> {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({ id: 'club-1' }) } } },
        { provide: OpeningHours, useClass: HttpOpeningHours },
      ],
    });
    fixture = TestBed.createComponent(DefineOpeningHours);
    server = TestBed.inject(HttpTestingController);
    page = fixture.nativeElement as HTMLElement;
    await fixture.whenStable();
    server.expectOne({ method: 'GET', url: OPENING_HOURS }).flush(sessions);
    await fixture.whenStable();
  }

  function save(): TestRequest {
    buttonNamed(page, 'Enregistrer les horaires').click();
    return server.expectOne({ method: 'PUT', url: OPENING_HOURS });
  }

  it('shows the administrator the opening hours already defined for the club', async () => {
    await openOpeningHours([{ day: 'FRIDAY', from: '20:00', to: '22:00', activity: 'jeu libre', venue: null }]);

    expect(fieldOfSession(page, 1, 'Jour').value).toBe('FRIDAY');
    expect(fieldOfSession(page, 1, 'De').value).toBe('20:00');
    expect(fieldOfSession(page, 1, 'À').value).toBe('22:00');
    expect(fieldOfSession(page, 1, 'Activité').value).toBe('jeu libre');
    expect(fieldOfSession(page, 1, 'À la salle de jeu du club').checked).toBe(true);
  });

  it('defines all the sessions of the club at once and tells the administrator so', async () => {
    await openOpeningHours([]);
    buttonNamed(page, 'Ajouter une session').click();
    await fixture.whenStable();
    choose(fieldOfSession(page, 1, 'Jour'), 'FRIDAY');
    fill(fieldOfSession(page, 1, 'De'), '20:00');
    fill(fieldOfSession(page, 1, 'À'), '22:00');
    fill(fieldOfSession(page, 1, 'Activité'), 'jeu libre');

    const request = save();
    request.flush(null);
    await fixture.whenStable();

    expect(request.request.body).toEqual([{ day: 'FRIDAY', from: '20:00', to: '22:00', activity: 'jeu libre', venue: null }]);
    expect(page.textContent).toContain("Les horaires d'ouverture ont été enregistrés.");
  });

  it('lets a session take place at another address than the playing venue, without activity', async () => {
    await openOpeningHours([]);
    buttonNamed(page, 'Ajouter une session').click();
    await fixture.whenStable();
    choose(fieldOfSession(page, 1, 'Jour'), 'MONDAY');
    fill(fieldOfSession(page, 1, 'De'), '20:00');
    fill(fieldOfSession(page, 1, 'À'), '22:00');
    uncheck(fieldOfSession(page, 1, 'À la salle de jeu du club'));
    await fixture.whenStable();
    fill(fieldOfSession(page, 1, 'Numéro et voie'), "3 rue de l'École");
    fill(fieldOfSession(page, 1, 'Code postal'), '45000');
    fill(fieldOfSession(page, 1, 'Localité'), 'Orléans');

    expect(save().request.body).toEqual([{
      day: 'MONDAY', from: '20:00', to: '22:00', activity: null,
      venue: { street: "3 rue de l'École", postcode: '45000', town: 'Orléans' },
    }]);
  });

  it('removes a session the administrator no longer wants', async () => {
    await openOpeningHours([
      { day: 'FRIDAY', from: '20:00', to: '22:00', activity: 'jeu libre', venue: null },
      { day: 'SATURDAY', from: '15:00', to: '17:00', activity: 'cours adultes', venue: null },
    ]);

    buttonNamed(page, 'Retirer la session 1').click();
    await fixture.whenStable();

    expect(save().request.body).toEqual([
      { day: 'SATURDAY', from: '15:00', to: '17:00', activity: 'cours adultes', venue: null },
    ]);
  });

  it('offers the activities proposed by the application', async () => {
    await openOpeningHours([{ day: 'FRIDAY', from: '20:00', to: '22:00', activity: null, venue: null }]);

    const list = fieldOfSession(page, 1, 'Activité').list;
    expect(Array.from(list?.options ?? []).map((option) => option.value))
      .toEqual(['jeu libre', 'cours adultes', 'cours enfants']);
  });

  it('tells the administrator when the opening hours are refused', async () => {
    await openOpeningHours([{ day: 'FRIDAY', from: '20:00', to: '22:00', activity: null, venue: null }]);

    save().flush(null, { status: 400, statusText: 'Bad Request' });
    await fixture.whenStable();

    expect(page.textContent).toContain("Les horaires d'ouverture n'ont pas été enregistrés");
  });
});

function fieldOfSession(page: HTMLElement, session: number, text: string): HTMLInputElement {
  const group = Array.from(page.querySelectorAll('fieldset')).find(
    (fieldset) => fieldset.querySelector('legend')?.textContent?.trim() === `Session ${session}`,
  );
  const label = Array.from(group?.querySelectorAll('label') ?? []).find(
    (l) => l.textContent?.trim() === text,
  );
  if (!label?.control) throw new Error(`No field "${text}" in session ${session}`);
  return label.control as HTMLInputElement;
}

function fill(field: HTMLInputElement, value: string): void {
  field.value = value;
  field.dispatchEvent(new Event('input'));
}

function choose(field: HTMLInputElement, value: string): void {
  field.value = value;
  field.dispatchEvent(new Event('change'));
}

function uncheck(field: HTMLInputElement): void {
  field.checked = false;
  field.dispatchEvent(new Event('change'));
}

function buttonNamed(page: HTMLElement, name: string): HTMLButtonElement {
  const button = Array.from(page.querySelectorAll('button')).find(
    (b) => (b.getAttribute('aria-label') ?? b.textContent?.trim()) === name,
  );
  if (!button) throw new Error(`No button named "${name}"`);
  return button;
}
