import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Allottee } from '../data/allottee';
import { Observable } from 'rxjs';
import { PageAllotte } from '../data/page-allotte';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AllotteeService {

  constructor(private snackBar: MatSnackBar,
              private httpClient: HttpClient) { }

  showMessage(msg: string): void {
    this.snackBar.open(msg, 'X', {
      duration: 3000,
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
  }

  create(allottee: Allottee): Observable<Allottee> {
    return this.httpClient.post<Allottee>(environment.baseUrlCad('/allottee'), allottee);
  }

  getAll(sort, direction, page, size): Observable<PageAllotte> {
    const params = new HttpParams()
      .append('page', page)
      .append('size', size)
      .append('sort', `${sort},${direction}`);

    return this.httpClient.get<PageAllotte>(environment.baseUrlCad('/allottee'), { params });
  }

  readByid(id: string): Observable<Allottee> {
    return this.httpClient.get<Allottee>(environment.baseUrlCad(`/allottee/${id}`));
  }
  
  readAllotteeTransactionById(id): Observable<Allottee> {
    return this.httpClient.get<Allottee>(environment.baseUrlAtm(`/transactional/allottee/${id}`));
  }

  edit(id: string, allottee: Allottee): Observable<Allottee> {
    return this.httpClient.put<Allottee>(environment.baseUrlCad(`/allottee/${id}`), allottee);
  }

  delete(id: string): Observable<any> {
    return this.httpClient.delete<any>(environment.baseUrlCad(`/allottee/${id}`));
  }

  reactive(allottee: Allottee): Observable<Allottee> {
    return this.httpClient.post<Allottee>(environment.baseUrlCad(`/allottee/active`), allottee);
  }
}
