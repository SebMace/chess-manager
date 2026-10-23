import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { Clubs } from './club-management/ports/clubs';
import { HttpClubs } from './club-management/http/http-clubs';
import { Communes } from './club-management/ports/communes';
import { HttpCommunes } from './club-management/http/http-communes';
import { OpeningHours } from './club-management/ports/opening-hours';
import { HttpOpeningHours } from './club-management/http/http-opening-hours';
import { Members } from './club-management/ports/members';
import { HttpMembers } from './club-management/http/http-members';
import { Towns } from './club-management/ports/towns';
import { HttpTowns } from './club-management/http/http-towns';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    { provide: Clubs, useClass: HttpClubs },
    { provide: Communes, useClass: HttpCommunes },
    { provide: Members, useClass: HttpMembers },
    { provide: OpeningHours, useClass: HttpOpeningHours },
    { provide: Towns, useClass: HttpTowns },
  ]
};
