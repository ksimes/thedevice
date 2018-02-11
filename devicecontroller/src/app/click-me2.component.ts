import { Component } from '@angular/core';
import {CommandService} from "./services/command.services";
import {Configuration} from "./model/configuration";

@Component({
  selector: 'app-click-me2',
  providers: [Configuration, CommandService],
  template: `
    <button (click)="onClickMe2($event)">Start device</button>
    {{clickMessage}}`
})
export class ClickMe2Component {
  clickMessage = '';
  clicks = 1;

  response : String;

  constructor(private _commandService: CommandService, public _configuration: Configuration) {
  }

  onClickMe2(event: any) {
    this.sendStart();
    this.clickMessage = 'Device has been started';
    let evtMsg = event ? ' Device has been started' : '';
    this.clickMessage = (`Click #${this.clicks++}. ${evtMsg}`);
  }
  onClickMe() {
  }

  private sendStart(): void {
    this._commandService.startDevice()
      .subscribe((data: String) => this.response = data,
        error => console.log(error),
        () => {
          console.log('Reset of device complete');
        }
      );
  }
}
