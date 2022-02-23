import { Component, OnInit } from '@angular/core';
import { AuthService } from '@auth0/auth0-angular';
/**
 * Reponsible for managing the user's authentication credentials. 
 * Will eventually be a proper JWT token obtained from an auth provider
 */
@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  loginName = 'nobody';
  profileJson: string = '';

  constructor(public auth: AuthService) {}

  ngOnInit(): void {
    this.auth.user$.subscribe(
      (profile) => { 
                       this.profileJson = JSON.stringify(profile, null, 2);
                       this.loginName = "TODO";
                   }
    );
  }

}
