import { environment } from "src/environments/environment";
import { IMicroservicio } from "./microservicios-settings";

export class EndPoints {
    
    static uriMicroServicio(microservicio: IMicroservicio, endpoint: string): string {
        const newEnvironment = `/`
        return newEnvironment.concat(microservicio.route) + endpoint;
    }
 
}
