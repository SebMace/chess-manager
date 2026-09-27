import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { App } from './app';
import { appConfig } from './app.config';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: appConfig.providers,
    })
      .compileComponents();
  });

  async function openApplication(): Promise<ComponentFixture<App>> {
    const fixture = TestBed.createComponent(App);
    await TestBed.inject(Router).navigateByUrl('/');
    await fixture.whenStable();
    return fixture;
  }

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('first lets the administrator consult the clubs of their committee', async () => {
    const page = (await openApplication()).nativeElement as HTMLElement;

    expect(labels(page)).toContain('Numéro de votre département');
  });

  it('lets the administrator go and create a club', async () => {
    const fixture = await openApplication();
    const page = fixture.nativeElement as HTMLElement;

    linkNamed(page, 'Créer un club').click();
    await fixture.whenStable();

    expect(labels(page)).toContain('Nom du club');
  });

  it('lets the administrator come back to the clubs of their committee', async () => {
    const fixture = await openApplication();
    const page = fixture.nativeElement as HTMLElement;
    linkNamed(page, 'Créer un club').click();
    await fixture.whenStable();

    linkNamed(page, 'Clubs').click();
    await fixture.whenStable();

    expect(labels(page)).toContain('Numéro de votre département');
  });
});

function labels(page: HTMLElement): (string | undefined)[] {
  return Array.from(page.querySelectorAll('label')).map(label => label.textContent?.trim());
}

function linkNamed(page: HTMLElement, text: string): HTMLAnchorElement {
  const link = Array.from(page.querySelectorAll('a')).find(a => a.textContent?.trim() === text);
  if (!link) throw new Error(`No link named "${text}"`);
  return link;
}
