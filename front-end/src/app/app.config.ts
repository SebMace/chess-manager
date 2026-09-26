import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { Clubs } from './club/clubs';
import { HttpClubs } from './club/http-clubs';
import { Communes } from './commune/communes';
import { HttpCommunes } from './commune/http-communes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    { provide: Clubs, useClass: HttpClubs },
    { provide: Communes, useClass: HttpCommunes },
  ]
};
