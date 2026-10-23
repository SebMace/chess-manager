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

  // Outer loop of the screen: enabled when the screen sends the competition and tells it has been created.
  it.skip('tells the administrator that the competition has been created with its information', async () => {
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
