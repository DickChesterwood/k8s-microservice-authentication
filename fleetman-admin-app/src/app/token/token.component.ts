import { Component, OnInit } from '@angular/core';

/**
 * Reponsible for managing the user's authentication credentials. 
 * Will eventually be a proper JWT token obtained from an auth provider
 */
@Component({
  selector: 'app-token',
  templateUrl: './token.component.html',
  styleUrls: ['./token.component.css']
})
export class TokenComponent implements OnInit {

  loginName = 'nobody';

  constructor() { }

  ngOnInit(): void {
  }

}
