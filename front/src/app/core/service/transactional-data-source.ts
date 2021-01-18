import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { Transactional } from '../data/transactional';
import { TransactionalService } from './transactional.service';
import { PageTransactional } from '../data/page-transactional';


export class TransactionalDataSource implements DataSource<Transactional> {

  private pageSubject = new BehaviorSubject<Transactional[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);

  public totalElements$ = new BehaviorSubject<number>(0);
  public loading$ = this.loadingSubject.asObservable();

  constructor(private service: TransactionalService) {}

  loadPage(id, sort, direction, pageIndex, pageSize): void {
    this.loadingSubject.next(true);

    this.service.getAll(id, sort, direction, pageIndex, pageSize)
      .pipe(
        catchError(() => of(new PageTransactional())),
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe(page => {
        this.pageSubject.next(page.content);
        this.totalElements$.next(page.totalElements);
      });
  }

  connect(collectionViewer: CollectionViewer): Observable<Transactional[]> {
    return this.pageSubject.asObservable();
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.pageSubject.complete();
    this.loadingSubject.complete();
  }
}
