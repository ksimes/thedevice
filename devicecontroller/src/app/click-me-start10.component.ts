import {Component} from '@angular/core';
import {CommandService} from "./services/command.services";
import {Configuration} from "./model/configuration";

@Component({
  selector: 'app-click-start10',
  providers: [Configuration, CommandService],
  template: `
    <button (click)="onClickMe2($event)">Start device with 10</button>
    {{clickMessage}}`
})
export class ClickStart10Component {
  clickMessage = '';
  clicks = 1;

  response: String;

  constructor(private _commandService: CommandService, public _configuration: Configuration) {
  }

  onClickMe2(event: any) {
    this.sendStart();
  }

  private sendStart(): void {
    this._commandService.startDeviceWith10()
      .subscribe((data: String) => this.response = data,
        error => console.log(error),
        () => {
          let evtMsg = event ? ' Device started with 11 mins' : '';
          this.clickMessage = (`${evtMsg}. Click #${this.clicks++}.`);
          console.log('Start of device complete');
        }
      );
  }
}
