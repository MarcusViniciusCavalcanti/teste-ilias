import { AfterViewInit, ViewChild } from '@angular/core';
import { Component, OnInit } from '@angular/core';
import { AllotteeDataSource } from '../../../core/service/allottee-data-source';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { AllotteeService } from '../../../core/service/allottee.service';
import { Allottee } from 'src/app/core/data/allottee';
import { merge } from 'rxjs';
import { tap } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { AllotteeDeleteComponent } from 'src/app/components/dialog/allottee-delete/allottee-delete.component';
import { ReactiveComponent } from '../../../components/dialog/allootee-reactive/reactive.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-allottee-read',
  templateUrl: './allottee-read.component.html',
  styleUrls: ['./allottee-read.component.css']
})
export class AllotteeReadComponent implements OnInit, AfterViewInit {

  dataSource: AllotteeDataSource;
  displayedColumns = ['id', 'name', 'email', 'cpf', 'retirement', 'amountYears', 'status', 'actions'];

  @ViewChild(MatPaginator) paginator: MatPaginator;

  @ViewChild(MatSort) sort: MatSort;

  constructor(private allotteeService: AllotteeService,
              private dialog: MatDialog,
              private router: Router) { }

  ngOnInit(): void {
    this.dataSource = new AllotteeDataSource(this.allotteeService);
    this.dataSource.loadPage('name', 'ASC', 0, 5);
  }

  ngAfterViewInit() {
    this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0);

    merge(this.sort.sortChange, this.paginator.page)
      .pipe(
        tap(() => this.loadPage())
      ).subscribe();
  }

  statusAsString(allottee: Allottee) {
    return Allottee.statusAsString(allottee.status);
  }

  isActive(allottee: Allottee) {
    return Allottee.isActive(allottee.status);
  }

  loadPage() {
    this.dataSource.loadPage(
        this.sort.active,
      this.sort.direction.toLocaleUpperCase(),
      this.paginator.pageIndex,
      this.paginator.pageSize);
  }

  openDeleteDialog(allottee: Allottee): void {
    const dialogRef = this.dialog.open(AllotteeDeleteComponent, {
      data: allottee
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.allotteeService.delete(result.id)
          .subscribe(
            () => {
              this.loadPage();
            },
            erro => {
                    console.error(erro);
            });
      }
    });
  }
  
  opetenReactiveDialog(allottee: Allottee): void {
    const dialog = this.dialog.open(ReactiveComponent, {
      data: allottee
    });
    
    dialog.afterClosed().subscribe(result => {
      console.log(result);
      if (result instanceof Allottee) {
        this.allotteeService.reactive(result).subscribe(() => {
          this.loadPage();
        }, error => {
          console.error(error);
          this.allotteeService.showMessage('Um erro aconteceu, verifique os dados e tente novamente')
        });
      } else {
        this.router.navigate(['/reativar', result]);
      }
    });
  }
}
