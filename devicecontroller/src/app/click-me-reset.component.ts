import {Component} from '@angular/core';
import {Configuration} from "./model/configuration";
import {CommandService} from "./services/command.services";

@Component({
  selector: 'app-click-reset',
  providers: [Configuration, CommandService],
  template: `
    <button (click)="onClickMe()">Reset</button>
    {{clickMessage}}`
})

export class ClickResetComponent {
  clickMessage = '';
  response: String;

  constructor(private _commandService: CommandService, public _configuration: Configuration) {
  }

  onClickMe() {
    this.sendReset();
  }

  private sendReset(): void {
    this._commandService.resetDevice()
      .subscribe((data: String) => this.response = data,
        error => console.log(error),
        () => {
          this.clickMessage = 'Device has been reset';
          console.log('Reset of device complete');
        }
      );
  }
}
