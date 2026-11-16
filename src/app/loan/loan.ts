import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Pageable } from '../core/model/page/Pageable';
import { Loan } from './model/Loan';
import { LOAN_DATA } from './model/mock-loans';
import { PaginatedData } from '../core/model/page/PaginatedData';
import { LOANS } from './model/mock-loans-list';

@Injectable({
    providedIn: 'root',
})
export class LoanService {
    constructor() { }

    getLoans(pageable?: Pageable, title?: string, client?: string, date?: Date, ):
     Observable<PaginatedData<Loan>> {
        return of(LOAN_DATA);
    }

    saveLoan(loan: Loan): Observable<void> {
        return of(null);
    }

    deleteLoan(idLoan: number): Observable<void> {
        return of(null);
    }
    getAllLoans(): Observable<Loan[]> {
        return of(LOANS);
    }
}
