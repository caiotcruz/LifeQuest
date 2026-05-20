import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { ActivityService } from '../../core/services/activity.service';
import { AuthService } from '../../core/services/auth.service';
import { Activity, UserActivity, ActivityCategory } from '../../core/models/models';

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

  // Estados locais reativos
  predefinedActivities = signal<Activity[]>([]);
  completedToday = signal<UserActivity[]>([]);
  isLoading = signal(true);

  // Categorias para agrupamento na UI
  categories: { id: ActivityCategory; label: string; icon: string }[] = [
    { id: 'HEALTH', label: 'Saúde e Fitness', icon: 'dumbbell-outline' },
    { id: 'STUDY', label: 'Estudos e Códigos', icon: 'code-working-outline' },
    { id: 'WORK', label: 'Trabalho e Projetos', icon: 'rocket-outline' },
    { id: 'PERSONAL_DEVELOPMENT', label: 'Evolução Pessoal', icon: 'brain-outline' },
    { id: 'CREATIVITY', label: 'Criatividade', icon: 'color-palette-outline' }
  ];

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.isLoading.set(true);
    
    // Busca o catálogo de atividades pré-definidas
    this.activityService.getPredefinedActivities().subscribe({
      next: (list) => this.predefinedActivities.set(list),
      error: (err) => console.error('Erro ao listar atividades', err)
    });

    // Busca o que o usuário já concluiu no dia de hoje
    this.activityService.getTodayActivities().subscribe({
      next: (logs) => {
        this.completedToday.set(logs);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Erro ao buscar histórico diário', err);
        this.isLoading.set(false);
      }
    });
  }

  getActivitiesByCategory(cat: ActivityCategory): Activity[] {
    return this.predefinedActivities().filter(a => a.category === cat);
  }

  isCompleted(activityId: number): boolean {
    return this.completedToday().some(log => log.activity.id === activityId);
  }

  onComplete(activity: Activity) {
    if (this.isCompleted(activity.id)) return;

    this.activityService.completeActivity({ activityId: activity.id }).subscribe({
      next: (result) => {
        // Atualiza instantaneamente os Signals globais do herói (Level, XP, Streak)
        this.authService.updateUser({
          totalXp: result.levelUpResult.totalXp,
          level: result.levelUpResult.newLevel,
          currentStreak: result.currentStreak
        });

        // Alerta visual de ganho de XP (Efeito psicológico de recompensa)
        console.log(`Missão cumprida: ${result.activityTitle}! +${result.xpEarned} XP`);
        
        // Recarrega os logs para travar o check de concluído na UI
        this.loadData();
      },
      error: (err) => console.error('Falha ao registrar conclusão de missão', err)
    });
  }
}