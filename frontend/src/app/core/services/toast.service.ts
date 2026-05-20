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
      cssClass: 'xp-toast',
      icon: 'flash-outline',
      animated: true
    });

    await toast.present();
  }

  async levelUp(newLevel: number): Promise<void> {
    const toast = await this.toastCtrl.create({
      message: `🎉 LEVEL UP! Você alcançou o Nível ${newLevel}!`,
      duration: 4000,
      position: 'middle',
      cssClass: 'levelup-toast',
      icon: 'trophy-outline',
      animated: true
    });

    await toast.present();
  }

  async badgeEarned(badgeTitle: string): Promise<void> {
    const toast = await this.toastCtrl.create({
      message: `🏆 Nova conquista desbloqueada: ${badgeTitle}`,
      duration: 3500,
      position: 'top',
      cssClass: 'badge-toast',
      icon: 'ribbon-outline',
      animated: true
    });

    await toast.present();
  }

  async streakUpdated(days: number): Promise<void> {
    const toast = await this.toastCtrl.create({
      message: `🔥 Sequência de ${days} ${days === 1 ? 'dia' : 'dias'} mantida!`,
      duration: 2500,
      position: 'top',
      cssClass: 'streak-toast',
      icon: 'flame-outline',
      animated: true
    });

    await toast.present();
  }

  async success(message: string): Promise<void> {
    const toast = await this.toastCtrl.create({
      message: `✅ ${message}`,
      duration: 2500,
      position: 'bottom',
      cssClass: 'success-toast',
      icon: 'checkmark-circle-outline',
      animated: true
    });

    await toast.present();
  }

  async error(message: string): Promise<void> {
    const toast = await this.toastCtrl.create({
      message: `❌ ${message}`,
      duration: 3500,
      position: 'bottom',
      cssClass: 'error-toast',
      icon: 'alert-circle-outline',
      animated: true
    });

    await toast.present();
  }
}