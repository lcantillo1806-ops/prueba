import { Injectable } from '@angular/core';
import { EndPoints } from './end-points';
import { microserviciosConfig } from './microservicios-settings';



@Injectable({
  providedIn: 'root'
})
export class AppSettingsService {
   
    public productoMS = {
        url: {
            getBase:EndPoints.uriMicroServicio(microserviciosConfig.productoMS(), ''),
            getBase2:EndPoints.uriMicroServicio(microserviciosConfig.productoMS(), '/'),
        }
    }
    
    public inventarioMS = {
        url: {
            getBase:EndPoints.uriMicroServicio(microserviciosConfig.inventarioMS(), '/'),
        }
    }
}


