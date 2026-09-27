import { Routes } from '@angular/router';
import { ClubsOfCommittee } from './club-management/clubs-of-committee/clubs-of-committee';
import { CreateClub } from './club-management/create-club/create-club';
import { DefineOpeningHours } from './club-management/define-opening-hours/define-opening-hours';

export const routes: Routes = [
  { path: '', component: ClubsOfCommittee, title: 'Clubs — Chess Manager' },
  { path: 'clubs/new', component: CreateClub, title: 'Créer un club — Chess Manager' },
  {
    path: 'clubs/:id/opening-hours',
    component: DefineOpeningHours,
    title: "Horaires d'ouverture — Chess Manager",
  },
];
