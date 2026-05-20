export interface LoginRequest {
  emailOrUsername: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: UserSummary;
}

export interface UserSummary {
  id: number;
  username: string;
  email: string;
  avatar: string | null;
  level: number;
  totalXp: number;
  currentStreak: number;
}

export type ActivityCategory = 'HEALTH' | 'STUDY' | 'WORK' | 'PERSONAL_DEVELOPMENT' | 'CREATIVITY';

export interface Activity {
  id: number;
  title: string;
  description: string | null;
  category: ActivityCategory;
  xpReward: number;
  iconName: string | null;
  isPredefined: boolean;
}

export interface UserActivity {
  id: number;
  activity: Activity;
  completedDate: string;
  completedAt: string;
  xpEarned: number;
  durationMinutes: number | null;
  notes: string | null;
}

export interface CompleteActivityRequest {
  activityId: number;
  completedDate?: string;
  durationMinutes?: number;
  notes?: string;
}

export interface LevelUpResult {
  leveledUp: boolean;
  previousLevel: number;
  newLevel: number;
  xpEarned: number;
  totalXp: number;
  xpForNextLevel: number;
  xpProgressInLevel: number;
}

export interface ActivityCompletionResult {
  userActivityId: number;
  activityTitle: string;
  xpEarned: number;
  levelUpResult: LevelUpResult;
  currentStreak: number;
  newBadgesEarned: any[];
}

export interface DashboardStats {
  xpWeek: number;
  xpMonth: number;
  xpYear: number;
  activeDaysThisMonth: number;
  dominantCategory: string | null;
  mostProductiveHour: number | null;
  activityHeatmap: Record<string, number>;
}

export type RecurrenceType = 'ONCE' | 'DAILY' | 'WEEKLY' | 'MONTHLY';

export interface ScheduleRequest {
  activityId: number;
  recurrenceType: RecurrenceType;
  daysOfWeek?: string[];
  daysOfMonth?: number[];
  startDate: string;
  endDate?: string;
}

export interface UserProfileResponse {
  id: number;
  username: string;
  email: string;
  avatar?: string;
  level: number;
  totalXp: number;
  currentStreak: number;
  longestStreak: number;
  createdAt: string;
  badges: any[];
}