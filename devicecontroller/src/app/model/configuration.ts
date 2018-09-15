export class Configuration {
  public applicationName:string = "Device Controller";

  public Host:string = window.location.hostname;
  public Port:string = window.location.port;
 //  public Server:string = "http://" + this.Host + ":" + this.Port;
  public Server:string = "http://" + this.Host + ":" + "4200";

  public BaseApiUrl:string = "/device";

  public resetCommand:string = "/reset";
  public startCommand:string = "/start";
  public startCommandWith30 :string = "/start30";
  public explodeCommand :string = "/explode";

  public ServerWithApiUrl = this.Server + this.BaseApiUrl;
}
