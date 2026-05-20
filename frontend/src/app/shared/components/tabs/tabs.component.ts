import { Component } from '@angular/core';
import { IonTabs, IonTabBar, IonTabButton, IonIcon, IonLabel } from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { homeOutline, flashOutline, calendarOutline, personOutline } from 'ionicons/icons';

@Component({
  selector: 'app-tabs',
  standalone: true,
  imports: [IonTabs, IonTabBar, IonTabButton, IonIcon, IonLabel],
  template: `
    <ion-tabs>
      <ion-tab-bar slot="bottom">
        <ion-tab-button tab="dashboard">
          <ion-icon name="home-outline" />
          <ion-label>Início</ion-label>
        </ion-tab-button>

        <ion-tab-button tab="activities">
          <ion-icon name="flash-outline" />
          <ion-label>Missões</ion-label>
        </ion-tab-button>

        <ion-tab-button tab="schedule">
          <ion-icon name="calendar-outline" />
          <ion-label>Agenda</ion-label>
        </ion-tab-button>

        <ion-tab-button tab="profile">
          <ion-icon name="person-outline" />
          <ion-label>Perfil</ion-label>
        </ion-tab-button>
      </ion-tab-bar>
    </ion-tabs>
  `
})
export class TabsComponent {
  constructor() {
    addIcons({ homeOutline, flashOutline, calendarOutline, personOutline });
  }
}