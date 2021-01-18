import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Transactional, Type } from '../../../core/data/transactional';
import { TransactionalService } from '../../../core/service/transactional.service';
import { Allottee } from '../../../core/data/allottee';

@Component({
  selector: 'app-transactional-aport-operation',
  templateUrl: './transactional-aport-operation.component.html',
  styleUrls: ['./transactional-aport-operation.component.css']
})
export class TransactionalAportOperationComponent implements OnInit {
  transaction = {
    id: 0,
    value: 0.0,
    type: '',
  };

  @Input() id: number;
  
  @Output() createNewTransactional =  new EventEmitter<boolean>();

  constructor(private transactionaService: TransactionalService) { }

  ngOnInit(): void {
  }

  send(transactional, type) {
    transactional.id = this.id;
    transactional.type = type;

    this.transactionaService.send(transactional).subscribe(result => {
      this.transactionaService.showMessage('Transação enviada com suces');
      this.createNewTransactional.emit(true);
    }, error => {
      console.error(error);
      this.transactionaService.showMessage('Error ao enviar nova transação');
    });
  }
}
