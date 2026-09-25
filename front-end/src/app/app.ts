import { Component } from '@angular/core';
import { CreateClub } from './club/create-club';

@Component({
  imports: [CreateClub],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {}
