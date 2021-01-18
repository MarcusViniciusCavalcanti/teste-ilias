import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface DialogData {
  id: number;
  name: string;
  cpf: string;
  email: string;
  retirement: number;
  amountYears: number;
}

@Component({
  selector: 'app-allootee-reactive',
  templateUrl: './allootee-reactive.component.html',
  styleUrls: ['./allootee-reactive.component.css']
})
export class ReactiveComponent {

  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) { }
}
