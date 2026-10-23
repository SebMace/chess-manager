import { Routes } from '@angular/router';
import { ClubsOfCommittee } from './club-management/clubs-of-committee/clubs-of-committee';
import { CreateClub } from './club-management/create-club/create-club';
import { DefineOpeningHours } from './club-management/define-opening-hours/define-opening-hours';
import { MembersOfClub } from './club-management/members-of-club/members-of-club';
import { CompetitionsOfSeason } from './competition/competitions-of-season/competitions-of-season';
import { CreateCompetition } from './competition/create-competition/create-competition';

export const routes: Routes = [
  { path: '', component: ClubsOfCommittee, title: 'Clubs — Chess Manager' },
  { path: 'clubs/new', component: CreateClub, title: 'Créer un club — Chess Manager' },
  {
    path: 'clubs/:id/opening-hours',
    component: DefineOpeningHours,
    title: "Horaires d'ouverture — Chess Manager",
  },
  { path: 'clubs/:id/members', component: MembersOfClub, title: 'Membres du club — Chess Manager' },
  { path: 'competitions', component: CompetitionsOfSeason, title: 'Compétitions — Chess Manager' },
  { path: 'competitions/new', component: CreateCompetition, title: 'Créer une compétition — Chess Manager' },
];
