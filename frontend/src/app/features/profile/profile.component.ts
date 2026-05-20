import { Component, inject, signal, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { addIcons } from 'ionicons';
import { personCircleOutline, createOutline, saveOutline, trophyOutline, logOutOutline } from 'ionicons/icons';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { UserProfileResponse } from '../../core/models/models';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, IonicModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  private toast = inject(ToastService);

  // Estados locais da tela
  profileData = signal<UserProfileResponse | null>(null);
  isLoading = signal<boolean>(false);
  isEditing = signal<boolean>(false);
  
  // Modelo temporário para edição do formulário
  editModel = {
    username: ''
  };

  constructor() {
    addIcons({ personCircleOutline, createOutline, saveOutline, trophyOutline, logOutOutline });
  }

  ngOnInit(): void {
    this.loadProfileData();
  }

  loadProfileData(): void {
    this.isLoading.set(true);
    this.authService.getProfile().subscribe({
      next: (data: UserProfileResponse) => {
        this.profileData.set(data);
        this.editModel.username = data.username;
        this.isLoading.set(false);
      },
      error: () => {
        this.toast.error('Erro ao carregar dados do perfil.'); // Limpo
        this.isLoading.set(false);
      }
    });
  }

  toggleEditMode(): void {
    if (this.isEditing()) {
      // Se estava editando, reseta para o valor original antes de cancelar
      this.editModel.username = this.profileData()?.username || '';
    }
    this.isEditing.set(!this.isEditing());
  }

  saveProfile(): void {
    if (this.editModel.username.length < 3) {
      this.toast.error('O nome de usuário deve ter no mínimo 3 caracteres.'); 
      return;
    }

    this.isLoading.set(true);
    
    this.authService.updateProfile({ username: this.editModel.username }).subscribe({
      next: (updatedData: UserProfileResponse) => {
        this.profileData.set(updatedData);
        // Sincroniza o Signal global de UI
        this.authService.updateUser({ username: updatedData.username });
        this.isEditing.set(false);
        this.isLoading.set(false);
        this.toast.streakUpdated(0); // Apenas reaproveitando a animação verde do toast para sucesso
      },
      error: () => {
        this.toast.error('Não foi possível salvar o novo nome.');
        this.isLoading.set(false);
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }
}