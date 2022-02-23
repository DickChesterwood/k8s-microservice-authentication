import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { TrucksComponent } from './trucks/trucks.component';
import { TokenComponent } from './token/token.component';

// From Auth0 authentication provider
import { AuthModule } from '@auth0/auth0-angular';
import { AuthButtonComponent } from './auth-button/auth-button.component';

@NgModule({
  declarations: [
    AppComponent,
    TrucksComponent,
    TokenComponent,
    AuthButtonComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    AuthModule.forRoot({
      domain: 'dev-ojpq3kdh.eu.auth0.com',
      clientId: 'fx68GMFbeQAmS0XoA5TMXjudJmg37gxm'
    })
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
