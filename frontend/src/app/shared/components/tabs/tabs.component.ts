import { Component } from '@angular/core';
import {
  RouterLink,
  RouterLinkActive,
  RouterOutlet
} from '@angular/router';

import { IonIcon } from '@ionic/angular/standalone';

import { addIcons } from 'ionicons';
import {
  homeOutline,
  checkmarkDoneOutline,
  calendarOutline,
  personOutline
} from 'ionicons/icons';

@Component({
  selector: 'app-tabs',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    IonIcon
  ],
  templateUrl: './tabs.component.html',
  styleUrls: ['./tabs.component.scss']
})
export class TabsComponent {
  constructor() {
    addIcons({
      homeOutline,
      checkmarkDoneOutline,
      calendarOutline,
      personOutline
    });
  }
}