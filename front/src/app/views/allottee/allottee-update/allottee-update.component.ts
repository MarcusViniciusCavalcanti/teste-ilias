import { Component, OnInit } from '@angular/core';
import { Allottee } from '../../../core/data/allottee';
import { AllotteeService } from '../../../core/service/allottee.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-allottee-update',
  template: `<app-form-allottee title="Editar Beneficiário"
                                [allottee]="allottee"
                                (saveAllotte)="update($event)">
            </app-form-allottee>`,
  styleUrls: ['./allottee-update.component.css']
})
export class AllotteeUpdateComponent implements OnInit {
  id: string;
  allottee: Allottee = {
    name: '',
    cpf: '',
    email: '',
    retirement: 0,
    amountYears: 0,
  };

  constructor(private allotteeService: AllotteeService,
              private route: ActivatedRoute,
              private router: Router) {
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    this.allotteeService.readByid(this.id).subscribe(allottee => this.allottee = allottee);
  }

  update(allottee: Allottee): void {
    this.allotteeService.edit(this.id, allottee).subscribe(() => {
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
