import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiHttpService {
  private http = inject(HttpClient);
  private readonly baseUrl = environment.apiBaseUrl;

  get<T>(url: string, params?: Record<string, any>): Observable<T> {
    let httpParams = this.getParam(params);

    return this.http.get<T>(`${this.baseUrl}${url}`, { params: httpParams });
  }

  private getParam(params: Record<string, any>) {
    let httpParams = new HttpParams();
    if (params) {
      Object.keys(params).forEach(key => {
        const value = params[key];
        if (value !== null && value !== undefined) {
          httpParams = httpParams.set(key, value);
        }
      });
    }
    return httpParams;
  }

  post<T>(url: string, body: any): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${url}`, body);
  }

  patch<T>(url: string, body: any): Observable<T> {
    return this.http.patch<T>(`${this.baseUrl}${url}`, body);
  }

  delete<T>(url: string, params?: Record<string, any>): Observable<T> {
    let httpParams = this.getParam(params);
    return this.http.delete<T>(`${this.baseUrl}${url}`, { params: httpParams });
  }
}


