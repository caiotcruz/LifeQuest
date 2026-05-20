import { Component, inject, computed, signal, OnInit, ViewChild, ElementRef, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { addIcons } from 'ionicons';
import { flame, sparklesOutline, calendarClearOutline, trendingUpOutline, timeOutline } from 'ionicons/icons';
import { AuthService } from '../../core/services/auth.service';
import { DashboardService } from '../../core/services/dashboard.service'; 
import { XpProgressComponent } from '../../shared/components/xp-progress/xp-progress.component';
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);

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

  @ViewChild('xpChartCanvas') xpChartCanvas!: ElementRef;
  private chartInstance: Chart | null = null;

  stats = signal<any>(null);
  isLoadingStats = signal<boolean>(false);

  currentUser = this.authService.currentUser;

  level = computed(() => this.currentUser()?.level || 1);
  totalXp = computed(() => this.currentUser()?.totalXp || 0);

  xpRequiredForCurrentLevel = computed(() => {
    const lvl = this.level();
    if (lvl <= 1) return 0;
    return Math.round(100 * Math.pow(lvl - 1, 1.5));
  });

  xpRequiredForNextLevel = computed(() => {
    return Math.round(100 * Math.pow(this.level(), 1.5));
  });

  progressXp = computed(() => {
    const base = this.xpRequiredForCurrentLevel();
    const currentProgress = this.totalXp() - base;
    return currentProgress >= 0 ? currentProgress : 0;
  });

  nextLevelXpThreshold = computed(() => {
    return this.xpRequiredForNextLevel() - this.xpRequiredForCurrentLevel();
  });

  constructor() {
    addIcons({ flame, sparklesOutline, calendarClearOutline, trendingUpOutline, timeOutline });

    effect(() => {
      const currentStats = this.stats();
      if (currentStats && currentStats.weeklyChart && this.xpChartCanvas) {
        this.renderChart(currentStats.weeklyChart);
      }
    });
  }

  ngOnInit(): void {
    this.loadAnalytics();
  }

  loadAnalytics(): void {
    this.isLoadingStats.set(true);
    
    this.dashboardService.getDashboardStats().subscribe({
      next: (data) => {
        this.stats.set(data);
        this.isLoadingStats.set(false);
      },
      error: () => {
        this.isLoadingStats.set(false);
      }
    });
  }

  private renderChart(chartData: any): void {
    if (this.chartInstance) {
      this.chartInstance.destroy(); 
    }

    this.chartInstance = new Chart(this.xpChartCanvas.nativeElement, {
      type: 'line',
      data: {
        labels: chartData.labels,
        datasets: [{
          label: 'XP Ganho',
          data: chartData.xpValues,
          borderColor: '#7C4DFF', 
          backgroundColor: 'rgba(124, 77, 255, 0.1)',
          fill: true,
          tension: 0.4,
          pointBackgroundColor: '#7C4DFF'
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false }
        },
        scales: {
          y: { beginAtZero: true, ticks: { precision: 0 } },
          x: { grid: { display: false } }
        }
      }
    });
  }
}