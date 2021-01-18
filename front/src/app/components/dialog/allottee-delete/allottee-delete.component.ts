import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface DialogData {
  name: string;
  id: number;
}

@Component({
  selector: 'app-allottee-delete',
  templateUrl: './allottee-delete.component.html',
  styleUrls: ['./allottee-delete.component.css']
})
export class AllotteeDeleteComponent {
  
  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData,
              public dialogRef: MatDialogRef<AllotteeDeleteComponent>) {
    
  }
  
  onNoClick() {
    this.dialogRef.close();
  }

}
