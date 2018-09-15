import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {ClickResetComponent} from './click-me-reset.component';
import {ClickStartComponent} from './click-me-start.component';
import {HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import {ClickStart30Component} from "./click-me-start30.component";
import {ClickExplodeComponent} from "./click-me-explode.component";

@NgModule({
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
  ],
  declarations: [
    AppComponent,
    ClickResetComponent,
    ClickStartComponent,
    ClickStart30Component,
    ClickExplodeComponent,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
