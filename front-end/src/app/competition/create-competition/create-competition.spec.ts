import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CreateCompetition } from './create-competition';
import { Competitions } from '../ports/competitions';
import { HttpCompetitions } from '../http/http-competitions';

describe('CreateCompetition', () => {
  let fixture: ComponentFixture<CreateCompetition>;
  let server: HttpTestingController;
  let page: HTMLElement;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Competitions, useClass: HttpCompetitions },
      ],
    });
    fixture = TestBed.createComponent(CreateCompetition);
    server = TestBed.inject(HttpTestingController);
    page = fixture.nativeElement as HTMLElement;
    await fixture.whenStable();
  });

  it('shows the fields needed to create a competition', () => {
    expect(page.querySelector('h2')?.textContent).toBe('Créer une compétition');
    expect(labels(page)).toEqual(['Nom de la compétition', 'Ligue organisatrice', "Nombre d'échiquiers"]);
    expect(buttonNamed(page, 'Créer la compétition')).toBeTruthy();
  });

  it('sends the competition typed by the administrator', () => {
    fill(fieldLabelled(page, 'Nom de la compétition'), 'Nationale 4');
    fill(fieldLabelled(page, 'Ligue organisatrice'), 'Centre-Val de Loire');
    fill(fieldLabelled(page, "Nombre d'échiquiers"), '6');

    buttonNamed(page, 'Créer la compétition').click();

    const request = server.expectOne({ method: 'POST', url: '/api/competitions' });
    expect(request.request.body).toEqual({ name: 'Nationale 4', organizingLeague: 'Centre-Val de Loire', boards: 6 });
  });

  it('tells the administrator that the competition has been created', async () => {
    fill(fieldLabelled(page, 'Nom de la compétition'), 'Nationale 4');
    fill(fieldLabelled(page, 'Ligue organisatrice'), 'Centre-Val de Loire');
    fill(fieldLabelled(page, "Nombre d'échiquiers"), '6');
    buttonNamed(page, 'Créer la compétition').click();

    server.expectOne({ method: 'POST', url: '/api/competitions' }).flush(null, { status: 201, statusText: 'Created' });
    await fixture.whenStable();

    expect(notice(page)).toBe('La compétition Nationale 4 a été créée.');
  });

  it('asks for the name of the competition and sends nothing without it', async () => {
    fill(fieldLabelled(page, 'Ligue organisatrice'), 'Centre-Val de Loire');
    fill(fieldLabelled(page, "Nombre d'échiquiers"), '6');

    buttonNamed(page, 'Créer la compétition').click();
    await fixture.whenStable();

    expect(page.textContent).toContain('Le nom de la compétition est obligatoire.');
    expect(fieldLabelled(page, 'Nom de la compétition').getAttribute('aria-invalid')).toBe('true');
    server.expectNone('/api/competitions');
  });

  it('asks for the organizing league and sends nothing without it', async () => {
    fill(fieldLabelled(page, 'Nom de la compétition'), 'Nationale 4');
    fill(fieldLabelled(page, "Nombre d'échiquiers"), '6');

    buttonNamed(page, 'Créer la compétition').click();
    await fixture.whenStable();

    expect(page.textContent).toContain('La ligue organisatrice est obligatoire.');
    expect(fieldLabelled(page, 'Ligue organisatrice').getAttribute('aria-invalid')).toBe('true');
    server.expectNone('/api/competitions');
  });

  it.each(['', '0', '2.5'])('asks for a whole number of boards of at least 1, not %j, and sends nothing', async (boards) => {
    fill(fieldLabelled(page, 'Nom de la compétition'), 'Nationale 4');
    fill(fieldLabelled(page, 'Ligue organisatrice'), 'Centre-Val de Loire');
    fill(fieldLabelled(page, "Nombre d'échiquiers"), boards);

    buttonNamed(page, 'Créer la compétition').click();
    await fixture.whenStable();

    expect(page.textContent).toContain("Le nombre d'échiquiers doit être un entier d'au moins 1.");
    expect(fieldLabelled(page, "Nombre d'échiquiers").getAttribute('aria-invalid')).toBe('true');
    server.expectNone('/api/competitions');
  });

  it('tells the administrator that the competition has been created with its information', async () => {
    fill(fieldLabelled(page, 'Nom de la compétition'), 'Nationale 4');
    fill(fieldLabelled(page, 'Ligue organisatrice'), 'Centre-Val de Loire');
    fill(fieldLabelled(page, "Nombre d'échiquiers"), '6');
    buttonNamed(page, 'Créer la compétition').click();

    const request = server.expectOne({ method: 'POST', url: '/api/competitions' });
    expect(request.request.body).toEqual({ name: 'Nationale 4', organizingLeague: 'Centre-Val de Loire', boards: 6 });
    request.flush(null, { status: 201, statusText: 'Created' });
    await fixture.whenStable();

    expect(notice(page)).toBe('La compétition Nationale 4 a été créée.');
  });
});

function labels(page: HTMLElement): (string | undefined)[] {
  return Array.from(page.querySelectorAll('label')).map(label => label.textContent?.trim());
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

function notice(page: HTMLElement): string | undefined {
  return page.querySelector('[role="status"]')?.textContent?.trim();
}
