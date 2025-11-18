

export interface IMicroservicio {
    route: string,
    port?: string
}

export class microserviciosConfig {

    /**
    * @description: Url end-point base
    */
    static productoMS(): IMicroservicio {
        const microServicio = { route: 'producto-ms/api/productos'}
        return microServicio
    }


    /**
    * @description: Url end-point base
    */
    static inventarioMS(): IMicroservicio {
        const microServicio = { route: 'api/inventarios/productos'}
        return microServicio
    }
}
