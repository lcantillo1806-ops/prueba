export interface IApiRes<T> {
     data: T;
    message: string;
    errors?: string[];
    code?: number
    jsonapi:{
        version: string
    }
}