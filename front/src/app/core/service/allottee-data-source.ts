import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { PageAllotte } from '../data/page-allotte';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { AllotteeService } from './allottee.service';
import { catchError, finalize } from 'rxjs/operators';
import { Allottee } from '../data/allottee';


export class AllotteeDataSource implements DataSource<Allottee> {

  private pageSubject = new BehaviorSubject<Allottee[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);

  public totalElements$ = new BehaviorSubject<number>(0);
  public loading$ = this.loadingSubject.asObservable();

  constructor(private service: AllotteeService) {}

  loadPage(sort, direction, pageIndex, pageSize): void {
    this.loadingSubject.next(true);

    this.service.getAll(sort, direction, pageIndex, pageSize)
      .pipe(
        catchError(() => of(new PageAllotte())),
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe(page => {
        this.pageSubject.next(page.content);
        this.totalElements$.next(page.totalElements);
      });
  }

  connect(collectionViewer: CollectionViewer): Observable<Allottee[]> {
    return this.pageSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.pageSubject.complete();
    this.loadingSubject.complete();
  }
}
