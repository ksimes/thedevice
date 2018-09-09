import {Component} from '@angular/core';
import {Configuration} from "./model/configuration";
import {CommandService} from "./services/command.services";

@Component({
  selector: 'app-click-explode',
  providers: [Configuration, CommandService],
  template: `
    <button (click)="onClickExplode()">Explode</button>
    {{clickMessage}}`
})

export class ClickExplodeComponent {
  clickMessage = '';
  response: String;

  constructor(private _commandService: CommandService, public _configuration: Configuration) {
  }

  onClickExplode() {
    this.sendExplode();
  }

  private sendExplode(): void {
    this._commandService.explodeDevice()
      .subscribe((data: String) => this.response = data,
        error => console.log(error),
        () => {
          this.clickMessage = 'Device has exploded';
          console.log('Explosion of device complete');
        }
      );
  }
}
