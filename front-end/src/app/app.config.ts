import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { Clubs } from './club-management/ports/clubs';
import { HttpClubs } from './club-management/http/http-clubs';
import { Communes } from './club-management/ports/communes';
import { HttpCommunes } from './club-management/http/http-communes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    { provide: Clubs, useClass: HttpClubs },
    { provide: Communes, useClass: HttpCommunes },
  ]
};
