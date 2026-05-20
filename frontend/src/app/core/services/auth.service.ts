import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { Preferences } from '@capacitor/preferences';
import { AuthResponse, LoginRequest, RegisterRequest, UserSummary, UserProfileResponse } from '../models/models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private _currentUser = signal<UserSummary | null>(null);
  private _isLoading = signal(false);

  readonly currentUser = this._currentUser.asReadonly();
  readonly isLoading = this._isLoading.asReadonly();
  readonly isLoggedIn = computed(() => this._currentUser() !== null);
  
  readonly level = computed(() => this._currentUser()?.level ?? 1);
  readonly streak = computed(() => this._currentUser()?.currentStreak ?? 0);
  readonly xp = computed(() => this._currentUser()?.totalXp ?? 0);

  private readonly API = `${environment.apiUrl}/auth`;
  private readonly ME_API = `${environment.apiUrl}/me`; // 👈 Rota base para o UserController do Java

  constructor(private http: HttpClient, private router: Router) {
    this.loadPersistedUser();
  }

  // ── Rotas de Perfil (Profile) ───────────────────────────

  getProfile(): Observable<UserProfileResponse> {
    return this.http.get<UserProfileResponse>(`${this.ME_API}/profile`);
  }

  updateProfile(request: { username?: string; avatar?: string }): Observable<UserProfileResponse> {
    return this.http.put<UserProfileResponse>(`${this.ME_API}/profile`, request);
  }

  // ── Rotas de Autenticação ───────────────────────────────

  register(request: RegisterRequest): Observable<AuthResponse> {
    this._isLoading.set(true);
    return this.http.post<AuthResponse>(`${this.API}/register`, request).pipe(
      tap(res => this.handleAuthSuccess(res)),
      catchError(err => {
        this._isLoading.set(false);
        return throwError(() => err);
      })
    );
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    this._isLoading.set(true);
    return this.http.post<AuthResponse>(`${this.API}/login`, request).pipe(
      tap(res => this.handleAuthSuccess(res)),
      catchError(err => {
        this._isLoading.set(false);
        return throwError(() => err);
      })
    );
  }

  async logout(): Promise<void> {
    await Preferences.remove({ key: 'token' });
    await Preferences.remove({ key: 'user' });
    this._currentUser.set(null);
    this.router.navigate(['/auth']); // Ajustado para a rota correta do app.routes.ts
  }

  async getToken(): Promise<string | null> {
    const { value } = await Preferences.get({ key: 'token' });
    return value;
  }

  // ── Atualização reativa do status do Herói (Sincroniza UI + Storage) ──
  updateUser(userData: Partial<UserSummary>): void {
    const current = this._currentUser();
    if (current) {
      const updated = { ...current, ...userData };
      this._currentUser.set(updated);
      Preferences.set({ key: 'user', value: JSON.stringify(updated) });
    }
  }

  private async handleAuthSuccess(res: AuthResponse): Promise<void> {
    await Preferences.set({ key: 'token', value: res.accessToken });
    await Preferences.set({ key: 'user', value: JSON.stringify(res.user) });
    this._currentUser.set(res.user);
    this._isLoading.set(false);
  }

  private async loadPersistedUser(): Promise<void> {
    const { value } = await Preferences.get({ key: 'user' });
    if (value) {
      this._currentUser.set(JSON.parse(value));
    }
  }
}