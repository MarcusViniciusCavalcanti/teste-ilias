import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { HeaderComponent } from './components/template/header/header.component';
import { NavComponent } from './components/template/nav/nav.component';
import { FooterComponent } from './components/template/footer/footer.component';
import { AllotteeComponent } from './views/allottee/allottee.component';
import { HomeComponent } from './views/home/home.component';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { RouterModule } from '@angular/router';
import { AppRoutingModule } from './app-routing.module';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { AllotteeCreateComponent } from './views/allottee/allottee-create/allottee-create.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { HttpClientModule } from '@angular/common/http';
import { AllotteeReadComponent } from './views/allottee/allottee-read/allottee-read.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSortModule } from '@angular/material/sort';
import { AllotteeUpdateComponent } from './views/allottee/allottee-update/allottee-update.component';
import { FormAllotteeComponent } from './components/form-allottee/form-allottee.component';
import { MatDialogModule } from '@angular/material/dialog';
import { AllotteeDeleteComponent } from './components/dialog/allottee-delete/allottee-delete.component';
import { TransactionalComponent } from './views/transactional/transactional.component';
import { TransactionalBalanceDetailsComponent } from './views/transactional/transactional-balance-details/transactional-balance-details.component';
import { TransactionalAportOperationComponent } from './views/transactional/transactional-aport-operation/transactional-aport-operation.component';
import { MatExpansionModule } from '@angular/material/expansion';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveComponent } from './components/dialog/allootee-reactive/reactive.component';
import { AllotteeReactiveComponent } from './views/allottee/allottee-reactive/allottee-reactive.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { DocumentationComponent } from './views/documentation/documentation.component';


@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    NavComponent,
    HomeComponent,
    AllotteeComponent,
    AllotteeCreateComponent,
    AllotteeReadComponent,
    AllotteeUpdateComponent,
    FormAllotteeComponent,
    AllotteeDeleteComponent,
    TransactionalComponent,
    TransactionalBalanceDetailsComponent,
    TransactionalAportOperationComponent,
    ReactiveComponent,
    AllotteeReactiveComponent,
    DocumentationComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    RouterModule,
    AppRoutingModule,
    MatCardModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSnackBarModule,
    MatButtonModule,
    HttpClientModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSortModule,
    MatDialogModule,
    MatExpansionModule,
    FlexLayoutModule,
    MatGridListModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
