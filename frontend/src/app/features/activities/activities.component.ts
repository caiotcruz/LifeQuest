import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { addIcons } from 'ionicons';
import { checkboxOutline, checkmarkCircle, addCircleOutline, barbellOutline, bookOutline, briefcaseOutline, personOutline, colorPaletteOutline } from 'ionicons/icons';
import { ActivityService } from '../../core/services/activity.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { Activity, UserActivity, CompleteActivityRequest, ActivityCompletionResult } from '../../core/models/models';

@Component({
  selector: 'app-activities',
  standalone: true,
  imports: [CommonModule, IonicModule],
  templateUrl: './activities.component.html',
  styleUrls: ['./activities.component.scss']
})
export class ActivitiesComponent implements OnInit {
  private activityService = inject(ActivityService);
  private authService = inject(AuthService);
  private toast = inject(ToastService);

  // Lista estática de apoio para o layout renderizar os blocos por categoria
  categories = [
    { id: 'HEALTH', label: 'Saúde & Fitness', icon: 'barbell-outline' },
    { id: 'STUDY', label: 'Estudos & Aprendizado', icon: 'book-outline' },
    { id: 'WORK', label: 'Trabalho & Foco', icon: 'briefcase-outline' },
    { id: 'PERSONAL_DEVELOPMENT', label: 'Desenvolvimento Pessoal', icon: 'person-outline' },
    { id: 'CREATIVITY', label: 'Criatividade & Hobbies', icon: 'color-palette-outline' }
  ];

  // Estados locais reativos (Signals)
  activities = signal<Activity[]>([]);
  completedTodayIds = signal<Set<number>>(new Set<number>());
  isLoading = signal<boolean>(false);

  // Calcula dinamicamente a quantidade de missões feitas hoje para mostrar no painel superior
  completedCount = computed(() => this.completedTodayIds().size);

  constructor() {
    // Registra os ícones do Ionic que serão desenhados na tela
    addIcons({ 
      checkboxOutline, checkmarkCircle, addCircleOutline, 
      barbellOutline, bookOutline, briefcaseOutline, 
      personOutline, colorPaletteOutline 
    });
  }

  ngOnInit(): void {
    this.loadDailyQuests();
  }

  loadDailyQuests(): void {
    this.isLoading.set(true);
    
    this.activityService.getTodayActivities().subscribe({
      next: (data: UserActivity[]) => {
        const completedIds = new Set<number>(
          data.filter(ua => ua.completedAt !== null && ua.completedAt !== undefined).map(ua => ua.activity.id)
        );
        this.completedTodayIds.set(completedIds);
        
        this.activityService.getPredefinedActivities().subscribe({
          next: (all: Activity[]) => {
            this.activities.set(all);
            this.isLoading.set(false);
          },
          error: () => {
            this.toast.error('Erro ao processar catálogo de missões.');
            this.isLoading.set(false);
          }
        });
      },
      error: () => {
        this.toast.error('Não foi possível carregar o quadro de missões.');
        this.isLoading.set(false);
      }
    });
  }

  // Métodos auxiliares de leitura chamados diretamente pelo HTML
  getActivitiesByCategory(categoryId: string): Activity[] {
    return this.activities().filter(act => act.category === categoryId);
  }

  isCompleted(activityId: number): boolean {
    return this.completedTodayIds().has(activityId);
  }

  completeActivity(activity: Activity): void {
    if (this.isCompleted(activity.id)) return;

    const request: CompleteActivityRequest = { activityId: activity.id };

    this.activityService.completeActivity(request).subscribe({
      next: async (result: ActivityCompletionResult) => {
        await this.toast.xpGained(result.xpEarned, result.activityTitle);

        this.authService.updateUser({
          totalXp: result.levelUpResult.totalXp,
          level: result.levelUpResult.newLevel,
          currentStreak: result.currentStreak
        });

        if (result.levelUpResult.leveledUp) {
          await this.toast.levelUp(result.levelUpResult.newLevel);
        }

        if (result.newBadgesEarned && result.newBadgesEarned.length > 0) {
          for (const badge of result.newBadgesEarned) {
            await this.toast.badgeEarned(badge.title);
          }
        }

        if (result.currentStreak > 0) {
          await this.toast.streakUpdated(result.currentStreak);
        }

        this.completedTodayIds.update(set => {
          const newSet = new Set(set);
          newSet.add(activity.id);
          return newSet;
        });
      },
      error: (err: any) => {
        const errorMsg = err.error?.message || 'Falha ao registrar atividade na guilda.';
        this.toast.error(errorMsg);
      }
    });
  }
}