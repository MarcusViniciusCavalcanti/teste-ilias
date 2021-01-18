import { Input, EventEmitter, Output } from '@angular/core';
import { Component, OnInit } from '@angular/core';
import { Allottee } from '../../core/data/allottee';

@Component({
  selector: 'app-form-allottee',
  templateUrl: './form-allottee.component.html',
  styleUrls: ['./form-allottee.component.css']
})
export class FormAllotteeComponent implements OnInit {
  @Input() allottee: Allottee;

  @Input() title: string;
  
  @Output() saveAllotte = new EventEmitter<Allottee>();

  constructor() { }

  ngOnInit(): void {
  }
  
  save(allotte: Allottee) {
    this.saveAllotte.emit(allotte);
  }
}
