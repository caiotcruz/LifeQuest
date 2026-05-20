import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  Activity, UserActivity, CompleteActivityRequest, 
  ActivityCompletionResult, ScheduleRequest 
} from '../models/models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ActivityService {
  private readonly API = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ── Catálogo de Atividades do Jogo ─────────────────────────

  getPredefinedActivities(): Observable<Activity[]> {
    return this.http.get<Activity[]>(`${this.API}/activities`);
  }

  // ── Histórico de Conclusões Diárias ────────────────────────

  getTodayActivities(): Observable<UserActivity[]> {
    return this.http.get<UserActivity[]>(`${this.API}/me/activities/today`);
  }

  completeActivity(request: CompleteActivityRequest): Observable<ActivityCompletionResult> {
    return this.http.post<ActivityCompletionResult>(
      `${this.API}/me/activities/complete`, 
      request
    );
  }

  // ── Agenda Gamificada (Schedules) ──────────────────────────

  createSchedule(request: ScheduleRequest): Observable<any> {
    return this.http.post<any>(`${this.API}/schedules`, request);
  }

  getActiveSchedules(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API}/schedules`);
  }
}