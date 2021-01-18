import { Component, OnInit, EventEmitter } from '@angular/core';
import { AllotteeService } from '../../../core/service/allottee.service';
import { Router } from '@angular/router';
import { Allottee } from '../../../core/data/allottee';

@Component({
  selector: 'app-allottee-create',
  template: `<app-form-allottee title="Novo Beneficiário"
                                [allottee]="allottee"
                                (saveAllotte)="create($event)">
            </app-form-allottee>`,
  styleUrls: ['./allottee-create.component.css']
})
export class AllotteeCreateComponent implements OnInit {

  allottee: Allottee = {
    name: '',
    cpf: '',
    email: '',
    retirement: 0,
    amountYears: 0,
  };

  constructor(private allotteeService: AllotteeService,
              private router: Router) { }

  ngOnInit(): void {
  }

  create(allottee: Allottee): void {
    this.allotteeService.create(allottee).subscribe(() => {
      this.allotteeService.showMessage('Operação executada com sucesso!');

      this.navigateToBack();
    }, error => {
      this.allotteeService.showMessage('Ocorreu um erro verifique as informações e tente novamente');
    });
  }

  navigateToBack(): void {
    this.router.navigate(['/beneficiarios']);
  }
}
