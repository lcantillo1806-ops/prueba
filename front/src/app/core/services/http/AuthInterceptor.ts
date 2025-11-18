import {
  HttpInterceptorFn
} from '@angular/common/http';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const cloned = req.clone({
    setHeaders: {
      'X-API-KEY': 'super-secret-key'
    }
  });
  return next(cloned);
};
