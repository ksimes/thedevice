import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {ClickResetComponent} from './click-me-reset.component';
import {ClickStartComponent} from './click-me-start.component';
import {HttpClientModule} from "@angular/common/http";
import {FormsModule} from "@angular/forms";
import {ClickStart10Component} from "./click-me-start10.component";
import {ClickStart15Component} from "./click-me-start15.component";
import {ClickStart20Component} from "./click-me-start20.component";
import {ClickStart25Component} from "./click-me-start25.component";
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
    ClickStart10Component,
    ClickStart15Component,
    ClickStart20Component,
    ClickStart25Component,
    ClickStart30Component,
    ClickExplodeComponent,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
