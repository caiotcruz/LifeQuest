import { Component, inject, computed, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { addIcons } from 'ionicons';
import { flame, sparklesOutline, calendarClearOutline, trendingUpOutline, timeOutline } from 'ionicons/icons';
import { AuthService } from '../../core/services/auth.service';
import { DashboardService } from '../../core/services/dashboard.service'; // 👈 Injetando o seu serviço de estatísticas
import { XpProgressComponent } from '../../shared/components/xp-progress/xp-progress.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, IonicModule, XpProgressComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private dashboardService = inject(DashboardService);

  // Estados locais reativos para as métricas do card de evolução
  stats = signal<any>(null);
  isLoadingStats = signal<boolean>(false);

  // Seleciona o sinal reativo do herói logado no sistema
  currentUser = this.authService.currentUser;

  // Mapeamento dos níveis com base na fórmula exponencial do backend (100 * level^1.5)
  level = computed(() => this.currentUser()?.level || 1);
  totalXp = computed(() => this.currentUser()?.totalXp || 0);

  // Calcula o XP acumulado até o início do nível atual
  xpRequiredForCurrentLevel = computed(() => {
    const lvl = this.level();
    if (lvl <= 1) return 0;
    return Math.round(100 * Math.pow(lvl - 1, 1.5));
  });

  // Calcula o XP total necessário para atingir o próximo nível
  xpRequiredForNextLevel = computed(() => {
    return Math.round(100 * Math.pow(this.level(), 1.5));
  });

  // XP que o usuário já ganhou dentro da faixa do nível atual
  progressXp = computed(() => {
    const base = this.xpRequiredForCurrentLevel();
    const currentProgress = this.totalXp() - base;
    return currentProgress >= 0 ? currentProgress : 0;
  });

  // Escopo de XP necessário para vencer o nível atual
  nextLevelXpThreshold = computed(() => {
    return this.xpRequiredForNextLevel() - this.xpRequiredForCurrentLevel();
  });

  constructor() {
    // 👈 Registra os ícones do Ionic para que eles apareçam nos cards de estatísticas
    addIcons({ flame, sparklesOutline, calendarClearOutline, trendingUpOutline, timeOutline });
  }

  ngOnInit(): void {
    this.loadAnalytics();
  }

  loadAnalytics(): void {
    this.isLoadingStats.set(true);
    
    // Busca os dados reais consolidados do UserController/DashboardController do Java
    this.dashboardService.getDashboardStats().subscribe({
      next: (data) => {
        this.stats.set(data);
        this.isLoadingStats.set(false);
      },
      error: () => {
        // Fallback seguro caso o backend do analytics ainda não tenha massa de dados
        this.isLoadingStats.set(false);
      }
    });
  }
}