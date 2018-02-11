import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {ClickMeComponent} from './click-me.component';
import {ClickMe2Component} from './click-me2.component';
import {HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";

@NgModule({
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
  ],
  declarations: [
    AppComponent,
    ClickMeComponent,
    ClickMe2Component,
  ],
  providers: [

  ],
  bootstrap: [ AppComponent ]
})
export class AppModule { }
