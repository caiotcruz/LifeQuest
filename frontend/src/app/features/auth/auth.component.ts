import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { IonicModule } from '@ionic/angular';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, IonicModule],
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss']
})
export class AuthComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  // Estados reativos da Tela (Angular Signals)
  isLoginMode = signal(true);
  errorMessage = signal<string | null>(null);
  isLoading = this.authService.isLoading;

  authForm: FormGroup;

  constructor() {
    this.authForm = this.fb.group({
      username: ['', [Validators.minLength(3), Validators.maxLength(50), Validators.pattern('^[a-zA-Z0-9_]+$')]],
      emailOrUsername: ['', [Validators.required]],
      email: ['', [Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });

    this.updateValidators();
  }

  toggleMode() {
    this.isLoginMode.update(mode => !mode);
    this.errorMessage.set(null);
    this.authForm.reset();
    this.updateValidators();
  }

  private updateValidators() {
    const usernameControl = this.authForm.get('username');
    const emailOrUsernameControl = this.authForm.get('emailOrUsername');
    const emailControl = this.authForm.get('email');

    if (this.isLoginMode()) {
      usernameControl?.clearValidators();
      emailControl?.clearValidators();
      emailOrUsernameControl?.setValidators([Validators.required]);
    } else {
      usernameControl?.setValidators([
        Validators.required, 
        Validators.minLength(3), 
        Validators.maxLength(50), 
        Validators.pattern('^[a-zA-Z0-9_]+$')
      ]);
      emailControl?.setValidators([Validators.required, Validators.email]);
      emailOrUsernameControl?.clearValidators();
    }

    usernameControl?.updateValueAndValidity();
    emailControl?.updateValueAndValidity();
    emailOrUsernameControl?.updateValueAndValidity();
  }

  onSubmit() {
    if (this.authForm.invalid) return;

    this.errorMessage.set(null);

    if (this.isLoginMode()) {
      this.authService.login({
        emailOrUsername: this.authForm.value.emailOrUsername,
        password: this.authForm.value.password
      }).subscribe({
        next: () => this.router.navigate(['/tabs/dashboard']), // 👈 Ajustado para a malha de abas
        error: (err) => this.errorMessage.set(err.error?.message || 'Credenciais inválidas.')
      });
    } else {
      this.authService.register({
        username: this.authForm.value.username,
        email: this.authForm.value.email,
        password: this.authForm.value.password
      }).subscribe({
        next: () => this.router.navigate(['/tabs/dashboard']), // 👈 Ajustado para a malha de abas
        error: (err) => this.errorMessage.set(err.error?.message || 'Falha ao registrar conta.')
      });
    }
  }
}