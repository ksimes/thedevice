
import { Component } from '@angular/core';
import {Configuration} from "./model/configuration";
import {CommandService} from "./services/command.services";

@Component({
  selector: 'app-click-me',
  providers: [Configuration, CommandService],
  template: `
    <button (click)="onClickMe()">Reset</button>
    {{clickMessage}}`
})

export class ClickMeComponent {
  clickMessage = '';
  response : String;

  constructor(private _commandService: CommandService, public _configuration: Configuration) {
  }

  onClickMe() {
    this.sendReset();
    this.clickMessage = 'Device has been reset';
  }

  private sendReset(): void {
    this._commandService.resetDevice()
      .subscribe((data: String) => this.response = data,
        error => console.log(error),
        () => {
          console.log('Reset of device complete');
        }
      );
  }
}
