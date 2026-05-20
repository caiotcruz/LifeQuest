import { Component, Input } from '@angular/core';
import { IonProgressBar } from '@ionic/angular/standalone';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-xp-progress',
  standalone: true,
  imports: [IonProgressBar, CommonModule],
  templateUrl: './xp-progress.component.html',
  styleUrls: ['./xp-progress.component.scss']
})
export class XpProgressComponent {
  @Input() level: number = 1;
  @Input() progressXp: number = 0;
  @Input() nextLevelXp: number = 100;

  get progressPercent(): number {
    if (this.nextLevelXp <= 0) return 0;
    return Math.min(this.progressXp / this.nextLevelXp, 1);
  }
}