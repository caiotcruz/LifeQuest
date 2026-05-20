import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Preferences } from '@capacitor/preferences';

export const authGuard: CanActivateFn = async () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }

  const { value } = await Preferences.get({ key: 'token' });
  if (value) {
    await authService.restoreSession();
    return true;
  }

  router.navigate(['/auth']);
  return false;
};