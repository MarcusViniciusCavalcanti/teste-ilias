import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { PageTransactional } from '../data/page-transactional';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Transactional } from '../data/transactional';

@Injectable({
  providedIn: 'root'
})
export class TransactionalService {
  constructor(private snackBar: MatSnackBar,
              private httpClient: HttpClient) {
  }
  
  showMessage(msg: string): void {
    this.snackBar.open(msg, 'X', {
      duration: 3000,
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
  }
  
  send(transactional: Transactional): Observable<Transactional> {
    return this.httpClient.post<Transactional>(environment.baseUrlAtm(`/transactional`), transactional);
  }
  
  getAll(id: number, sort, direction, page, size): Observable<PageTransactional> {
    const params = new HttpParams()
      .append('page', page)
      .append('size', size)
      .append('sort', `${sort},${direction}`);
    return this.httpClient.get<PageTransactional>(environment.baseUrlAtm(`/transactional/${id}`), { params });
  }
}
