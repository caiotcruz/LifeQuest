import { Injectable } from '@angular/core';
import { ToastController } from '@ionic/angular/standalone';

@Injectable({ providedIn: 'root' })
export class ToastService {
  constructor(private toastCtrl: ToastController) {}

  async xpGained(xp: number, activityName: string): Promise<void> {
    const toast = await this.toastCtrl.create({
      message: `⚡ +${xp} XP — ${activityName}`,
      duration: 2500,
      position: 'top',
      color: 'warning',
      cssClass: 'xp-toast'
    });
    await toast.present();
  }

  async levelUp(newLevel: number): Promise<void> {
    const toast = await this.toastCtrl.create({
      message: `🎉 LEVEL UP! Você alcançou o Nível ${newLevel}!`,
      duration: 4000,
      position: 'middle',
      color: 'success',
      cssClass: 'levelup-toast'
    });
    await toast.present();
  }

  async badgeEarned(badgeTitle: string): Promise<void> {
    const toast = await this.toastCtrl.create({
      message: `🏆 Conquista Desbloqueada: ${badgeTitle}`,
      duration: 3500,
      position: 'top',
      color: 'tertiary'
    });
    await toast.present();
  }

  async streakUpdated(days: number): Promise<void> {
    const toast = await this.toastCtrl.create({
      message: `🔥 Streak de ${days} ${days === 1 ? 'dia' : 'dias'} mantida!`,
      duration: 2000,
      position: 'top',
      color: 'danger'
    });
    await toast.present();
  }

  async error(message: string): Promise<void> {
    const toast = await this.toastCtrl.create({
      message: `❌ ${message}`,
      duration: 3000,
      position: 'bottom',
      color: 'danger'
    });
    await toast.present();
  }
}