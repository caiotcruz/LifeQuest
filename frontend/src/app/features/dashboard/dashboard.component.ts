import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { AuthService } from '../../core/services/auth.service';
import { DashboardService } from '../../core/services/dashboard.service';
import { DashboardStats } from '../../core/models/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, IonicModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  // Injeção de dependências do Core
  public authService = inject(AuthService);
  private dashboardService = inject(DashboardService);

  // Estados locais reativos
  stats = signal<DashboardStats | null>(null);
  isLoadingStats = signal(true);

  ngOnInit() {
    this.loadDashboardData();
  }

  loadDashboardData() {
    this.isLoadingStats.set(true);
    this.dashboardService.getDashboardStats().subscribe({
      next: (data) => {
        this.stats.set(data);
        this.isLoadingStats.set(false);
      },
      error: (err) => {
        console.error('Falha ao carregar métricas do dashboard', err);
        this.isLoadingStats.set(false);
      }
    });
  }
}