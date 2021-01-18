import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionalBalanceDetailsComponent } from './transactional-balance-details.component';

describe('TransactionalBalanceDetailsComponent', () => {
  let component: TransactionalBalanceDetailsComponent;
  let fixture: ComponentFixture<TransactionalBalanceDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransactionalBalanceDetailsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionalBalanceDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
