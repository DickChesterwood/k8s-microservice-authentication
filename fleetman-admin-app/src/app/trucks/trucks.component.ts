import { Component, OnInit } from '@angular/core';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
@Component({
  selector: 'app-trucks',
  templateUrl: './trucks.component.html',
  styleUrls: ['./trucks.component.css']
})
@Injectable()
export class TrucksComponent implements OnInit {

  trucks: string = '-- no data yet --';

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
  }

  populateTrucks() {
    // Call the API Gateway - need a token - but this will be added automatically.
    this.http.get<string>("http://localhost:8080/vehicles").subscribe((data: string) => this.trucks = data);    
  }

}
