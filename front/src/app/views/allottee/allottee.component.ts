import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-allottee',
  templateUrl: './allottee.component.html',
  styleUrls: ['./allottee.component.css']
})
export class AllotteeComponent implements OnInit {
  
  constructor(private router: Router) { }
  
  ngOnInit(): void {
  }
  
  navigateToCreate(): void {
    this.router.navigate(['/beneficiarios/criar']);
  }
}
