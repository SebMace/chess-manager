import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { Clubs } from './club/clubs';
import { HttpClubs } from './club/http-clubs';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    { provide: Clubs, useClass: HttpClubs },
  ]
};
