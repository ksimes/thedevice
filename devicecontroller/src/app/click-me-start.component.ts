import {Component} from '@angular/core';
import {CommandService} from "./services/command.services";
import {Configuration} from "./model/configuration";

@Component({
  selector: 'app-click-start',
  providers: [Configuration, CommandService],
  template: `
    <button (click)="onClickMe2($event)">Start device</button>
    {{clickMessage}}`
})
export class ClickStartComponent {
  clickMessage = '';
  clicks = 1;

  response: String;

  constructor(private _commandService: CommandService, public _configuration: Configuration) {
  }

  onClickMe2(event: any) {
    this.sendStart();
  }

  private sendStart(): void {
    this._commandService.startDevice()
      .subscribe((data: String) => this.response = data,
        error => console.log(error),
        () => {
          let evtMsg = event ? ' Device has been started' : '';
          this.clickMessage = (`${evtMsg}. Click #${this.clicks++}.`);
          console.log('Start of device complete');
        }
      );
  }
}
