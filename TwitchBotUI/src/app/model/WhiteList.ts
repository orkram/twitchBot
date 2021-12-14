export class WhiteList {
  id: string;
  allowedDomain: string;


  constructor(id: string, allowedDomain: string) {
    this.id = id;
    this.allowedDomain = allowedDomain;

  }
}
