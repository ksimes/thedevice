import {Injectable} from '@angular/core';
import {Configuration} from '../model/configuration';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from "@angular/common/http";

@Injectable()
export class CommandService {

  private actionUrl: string;
  private configuration: Configuration;

  constructor(private _http: HttpClient, private _configuration: Configuration) {

    this.actionUrl = _configuration.ServerWithApiUrl;
    this.configuration = _configuration;
  }

  public startDevice = (): Observable<String> => {
    return this._http.get<String>(this.actionUrl + this.configuration.startCommand);
  };

  public startDeviceWith30 = (): Observable<String> => {
    return this._http.get<String>(this.actionUrl + this.configuration.startCommandWith30);
  };

  public resetDevice = (): Observable<String> => {
    return this._http.get<String>(this.actionUrl + this.configuration.resetCommand);
  };

  public explodeDevice = (): Observable<String> => {
    return this._http.get<String>(this.actionUrl + this.configuration.explodeCommand);
  };
}
