import { Component, Input } from '@angular/core';
import { IonProgressBar } from '@ionic/angular/standalone';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-xp-progress',
  standalone: true,
  imports: [IonProgressBar, CommonModule],
  template: `
    <div class="xp-progress-container">
      <div class="level-info">
        <span class="level-label">Nível {{ level }}</span>
        <span class="xp-label">{{ progressXp }} / {{ nextLevelXp }} XP</span>
      </div>
      <ion-progress-bar
        [value]="progressPercent"
        color="warning"
        class="xp-bar">
      </ion-progress-bar>
    </div>
  `,
  styles: [`
    .xp-progress-container {
      padding: 8px 0;
    }
    .level-info {
      display: flex;
      justify-content: space-between;
      margin-bottom: 6px;
      font-size: 13px;
      font-weight: 600;
      color: var(--ion-color-medium);
    }
    .level-label {
      font-weight: 700;
      color: var(--ion-color-warning);
    }
    .xp-bar {
      height: 10px;
      border-radius: 5px;
      --background: rgba(255, 179, 71, 0.15);
    }
  `]
})
export class XpProgressComponent {
  @Input() level: number = 1;
  @Input() progressXp: number = 0;
  @Input() nextLevelXp: number = 100;

  // Calcula dinamicamente a fração de preenchimento (entre 0.0 e 1.0) que o Ionic espera
  get progressPercent(): number {
    if (this.nextLevelXp <= 0) return 0;
    return Math.min(this.progressXp / this.nextLevelXp, 1);
  }
}