import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { addIcons } from 'ionicons';
import { add, calendarOutline, trashOutline, closeOutline, saveOutline } from 'ionicons/icons';
import { ActivityService } from '../../core/services/activity.service';
import { ToastService } from '../../core/services/toast.service';
import { Activity, ScheduleRequest } from '../../core/models/models';

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [CommonModule, IonicModule, FormsModule],
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.scss']
})
export class ScheduleComponent implements OnInit {
  private activityService = inject(ActivityService);
  private toast = inject(ToastService);

  // Estados
  schedules = signal<any[]>([]);
  activitiesCatalog = signal<Activity[]>([]);
  isLoading = signal<boolean>(false);
  isModalOpen = signal<boolean>(false);

  // Modelo do formulário de novo agendamento
  newSchedule: Partial<ScheduleRequest> = {
    recurrenceType: 'DAILY',
    startDate: new Date().toISOString().split('T')[0] // Data de hoje no formato YYYY-MM-DD
  };

  constructor() {
    addIcons({ add, calendarOutline, trashOutline, closeOutline, saveOutline });
  }

  ngOnInit(): void {
    this.loadSchedules();
    this.loadCatalog();
  }

  loadSchedules(): void {
    this.isLoading.set(true);
    this.activityService.getActiveSchedules().subscribe({
      next: (data) => {
        this.schedules.set(data);
        this.isLoading.set(false);
      },
      error: () => {
        this.toast.error('Erro ao carregar sua agenda.');
        this.isLoading.set(false);
      }
    });
  }

  loadCatalog(): void {
    this.activityService.getPredefinedActivities().subscribe({
      next: (catalog) => this.activitiesCatalog.set(catalog)
    });
  }

  openModal(): void {
    this.isModalOpen.set(true);
  }

  closeModal(): void {
    this.isModalOpen.set(false);
    this.resetForm();
  }

  resetForm(): void {
    this.newSchedule = {
      recurrenceType: 'DAILY',
      startDate: new Date().toISOString().split('T')[0]
    };
  }

  saveSchedule(): void {
    if (!this.newSchedule.activityId) {
      this.toast.error('Selecione uma atividade do catálogo.'); // Limpo
      return;
    }
    const payload = this.newSchedule as ScheduleRequest;
    this.activityService.createSchedule(payload).subscribe({
      next: () => {
        this.toast.badgeEarned('Rotina Agendada!'); // Limpo
        this.loadSchedules();
        this.closeModal();
      },
      error: () => this.toast.error('Falha ao agendar.')
    });
  }
}