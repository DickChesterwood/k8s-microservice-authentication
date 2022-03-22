import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { TrucksComponent } from './trucks/trucks.component';
import { UserComponent } from './user/user.component';

// From Auth0 authentication provider
import { AuthModule } from '@auth0/auth0-angular';
import { AuthButtonComponent } from './auth-button/auth-button.component';

// More from Auth0, see https://auth0.com/docs/quickstart/spa/angular/02-calling-an-api
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthHttpInterceptor } from '@auth0/auth0-angular';


// Calling APIs
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent,
    TrucksComponent,
    UserComponent,
    AuthButtonComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    AuthModule.forRoot({
      domain: 'dev-ojpq3kdh.eu.auth0.com',
      clientId: 'fx68GMFbeQAmS0XoA5TMXjudJmg37gxm',
      audience: 'https://fleetman.chesterwood.io',    // This is for logging in
      scope: 'read:truck',
      // Specify configuration for the interceptor              
      httpInterceptor: {
        allowedList: [
          {
            // Which urls to intercept - this will be the entire API 
            uri: 'http://localhost:8080/*',
            tokenOptions: {
              clientId: 'sfdfs', // ? API id
              // The attached token should target this audience
              audience: 'https://fleetman.chesterwood.io',    // this is for API calls

              // The attached token should have these scopes
              scope: 'read:truck'
            }
          }
        ]
      }
    })
  ],
  providers: [  { provide: HTTP_INTERCEPTORS, useClass: AuthHttpInterceptor, multi: true } ],
  bootstrap: [AppComponent]
})
export class AppModule { }
