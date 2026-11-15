import { inject, Injectable } from '@angular/core';
import { Service } from '@angular/core';
import { Observable, of } from 'rxjs';
import { CLIENTS_DATA } from "./model/mock-clients";
import { Clients } from './model/Clients';

@Injectable({
    providedIn: 'root'  
})
export class ClientsService {
    constructor() { }

    getClients(): Observable<Clients[]> {
        return of(CLIENTS_DATA);
    }

    saveClient(client: Clients): Observable<Clients> {
        return of(null);
    }

    deleteClient(idClient: number): Observable<any> {
        return of(null);
    }
}
