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

  it('tells the administrator that the club has been created', async () => {
    createClubNamed(page, 'Montargis');

    const request = server.expectOne({ method: 'POST', url: '/clubs' });
    expect(request.request.body).toEqual({ name: 'Montargis' });
    request.flush(null, { status: 201, statusText: 'Created', headers: { Location: '/clubs/6' } });
    await fixture.whenStable();

    expect(page.textContent).toContain('Le club Montargis a été créé.');
  });

  it('tells the administrator that the club could not be created', async () => {
    createClubNamed(page, 'Montargis');

    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 500, statusText: 'Internal Server Error' });
    await fixture.whenStable();

    expect(page.textContent).not.toContain('Le club Montargis a été créé.');
    expect(page.textContent).toContain("Le club n'a pas pu être créé. Réessayez.");
  });

  it('no longer tells the administrator that the club could not be created once it has been created', async () => {
    createClubNamed(page, 'Montargis');
    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 500, statusText: 'Internal Server Error' });
    await fixture.whenStable();

    createClubNamed(page, 'Montargis');
    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 201, statusText: 'Created', headers: { Location: '/clubs/6' } });
    await fixture.whenStable();

    expect(page.textContent).toContain('Le club Montargis a été créé.');
    expect(page.textContent).not.toContain("Le club n'a pas pu être créé. Réessayez.");
  });

  it('no longer tells the administrator that a previous club has been created once a creation fails', async () => {
    createClubNamed(page, 'Montargis');
    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 201, statusText: 'Created', headers: { Location: '/clubs/6' } });
    await fixture.whenStable();

    createClubNamed(page, 'Olivet');
    server.expectOne({ method: 'POST', url: '/clubs' })
      .flush(null, { status: 500, statusText: 'Internal Server Error' });
    await fixture.whenStable();

    expect(page.textContent).toContain("Le club n'a pas pu être créé. Réessayez.");
    expect(page.textContent).not.toContain('Le club Montargis a été créé.');
  });
});

function createClubNamed(page: HTMLElement, name: string): void {
  const field = fieldLabelled(page, 'Nom du club');
  field.value = name;
  field.dispatchEvent(new Event('input'));
  buttonNamed(page, 'Créer le club').click();
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
