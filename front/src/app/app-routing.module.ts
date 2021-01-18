import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './views/home/home.component';
import { AllotteeComponent } from './views/allottee/allottee.component';
import { AllotteeCreateComponent } from './views/allottee/allottee-create/allottee-create.component';
import { AllotteeUpdateComponent } from './views/allottee/allottee-update/allottee-update.component';
import { TransactionalComponent } from './views/transactional/transactional.component';
import { AllotteeReactiveComponent } from './views/allottee/allottee-reactive/allottee-reactive.component';
import { DocumentationComponent } from './views/documentation/documentation.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'beneficiarios',
    component: AllotteeComponent
  },
  {
    path: 'beneficiarios/criar',
    component: AllotteeCreateComponent
  },
  {
    path: 'beneficiarios/atualizar/:id',
    component: AllotteeUpdateComponent
  },
  {
    path: 'transacoes/:id',
    component: TransactionalComponent,
  },
  {
    path: 'reativar/:id',
    component: AllotteeReactiveComponent,
  },
  {
    path: 'documentacao',
    component: DocumentationComponent,
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
