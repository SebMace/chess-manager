import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CreateClub } from './create-club';

describe('CreateClub', () => {
  it('tells the administrator that the club has been created', async () => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const fixture = TestBed.createComponent(CreateClub);
    const server = TestBed.inject(HttpTestingController);
    const page = fixture.nativeElement as HTMLElement;
    await fixture.whenStable();

    createClubNamed(page, 'Montargis');

    const request = server.expectOne({ method: 'POST', url: '/clubs' });
    expect(request.request.body).toEqual({ name: 'Montargis' });
    request.flush(null, { status: 201, statusText: 'Created', headers: { Location: '/clubs/6' } });
    await fixture.whenStable();

    expect(page.textContent).toContain('Le club Montargis a été créé.');
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
