export class Configuration {
  public applicationName:string = "Device Controller";

  public Host:string = window.location.hostname;
  public Port:string = window.location.port;
 //  public Server:string = "http://" + this.Host + ":" + this.Port;
  public Server:string = "http://" + this.Host + ":" + "4200";

  public BaseApiUrl:string = "/device";

  public resetCommand:string = "/reset";
  public startCommand:string = "/start";
  public startCommandWith10 :string = "/start10";
  public startCommandWith15 :string = "/start15";
  public startCommandWith20 :string = "/start20";
  public startCommandWith25 :string = "/start25";
  public startCommandWith30 :string = "/start30";
  public explodeCommand :string = "/explode";

  public ServerWithApiUrl = this.Server + this.BaseApiUrl;
}
