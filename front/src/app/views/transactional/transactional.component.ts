import { Component, OnInit, ViewChild } from '@angular/core';
import { AllotteeService } from '../../core/service/allottee.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Allottee } from '../../core/data/allottee';
import { MatPaginator } from '@angular/material/paginator';
import { TransactionalDataSource } from 'src/app/core/service/transactional-data-source';
import { TransactionalService } from '../../core/service/transactional.service';
import { Transactional, Type } from '../../core/data/transactional';
import { merge } from 'rxjs';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'app-transactional',
  templateUrl: './transactional.component.html',
  styleUrls: ['./transactional.component.css']
})
export class TransactionalComponent implements OnInit {
  allottee: Allottee = new Allottee();
  dataSource: TransactionalDataSource;
  id: string;

  displayedColumns = ['id', 'value', 'date', 'status'];

  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(private allotteeService: AllotteeService,
              private transactionaService: TransactionalService,
              private route: ActivatedRoute,
              private router: Router) { }

  ngOnInit() {
    this.id = this.route.snapshot.paramMap.get('id');
    this.dataSource = new TransactionalDataSource(this.transactionaService);
    this.dataSource.loadPage(this.id, 'date', 'DESC', 0, 3);
    this.allotteeService.readAllotteeTransactionById(this.id)
      .subscribe(response => {
        if (Allottee.isActive(response.status)) {
          this.allottee = response;
        } else {
          this.showMessage('Beneficiário não disponível para transações');
        }
      }, error => {
        console.error(error);
        this.showMessage('Ocorreu um error, por favor tente mais tarde');
      });
  }

  showMessage(msg): void {
    this.allotteeService.showMessage(msg);
  }

  isIncrement(transactional: Transactional): boolean {
    return transactional.transactionalType === 'INCREMENT';
  }

  loadPage(event: boolean) {
    if (event) {
      this.dataSource.loadPage(
        this.id,
        'date',
        'DESC',
        this.paginator.pageIndex,
        this.paginator.pageSize);
    }
  }
  
  ngAfterViewInit() {
    merge(this.paginator.page)
      .pipe(
        tap(() => this.loadPage(true))
      ).subscribe();
  }
}
