import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CompetitionsOfSeason } from './competitions-of-season';
import { Competitions } from '../ports/competitions';
import { HttpCompetitions } from '../http/http-competitions';

const COMPETITIONS = '/api/competitions';
const CURRENT_SEASON = { yearBegin: 2026, yearEnd: 2027 };

const NATIONALE_4 = { name: 'Nationale 4', organizingLeague: 'Centre-Val de Loire', boards: 6 };

describe('CompetitionsOfSeason', () => {
  let fixture: ComponentFixture<CompetitionsOfSeason>;
  let page: HTMLElement;

  async function openCompetitions(competitions: unknown[]): Promise<void> {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Competitions, useClass: HttpCompetitions },
      ],
    });
    fixture = TestBed.createComponent(CompetitionsOfSeason);
    const server = TestBed.inject(HttpTestingController);
    page = fixture.nativeElement as HTMLElement;
    await fixture.whenStable();
    server.expectOne({ method: 'GET', url: COMPETITIONS }).flush({ season: CURRENT_SEASON, competitions });
    await fixture.whenStable();
  }

  it('shows the administrator the competitions of the season under that season', async () => {
    await openCompetitions([NATIONALE_4]);

    expect(page.querySelector('h2')?.textContent).toBe('Compétitions de la saison 2026-2027');
    expect(shownCompetitions(page)).toEqual([['Nationale 4', 'Centre-Val de Loire', '6']]);
  });

  it('shows the competitions in the order the back-end gives them, without sorting them again', async () => {
    await openCompetitions([
      { name: 'Régionale 1', organizingLeague: 'Centre-Val de Loire', boards: 4 },
      NATIONALE_4,
    ]);

    expect(shownCompetitions(page).map((row) => row[0])).toEqual(['Régionale 1', 'Nationale 4']);
  });

  it('tells the administrator that the season has no competition yet, and still names the season', async () => {
    await openCompetitions([]);

    expect(page.querySelector('h2')?.textContent).toBe('Compétitions de la saison 2026-2027');
    expect(page.textContent).toContain('Aucune compétition pour la saison 2026-2027.');
    expect(page.querySelector('table')).toBeNull();
  });
});

function shownCompetitions(page: HTMLElement): string[][] {
  return Array.from(page.querySelectorAll('tbody tr')).map((row) =>
    Array.from(row.querySelectorAll('th, td')).map((cell) => cell.textContent?.trim() ?? ''),
  );
}
