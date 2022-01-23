export class UserCommand {
  id: number;
  signature: string;
  output: string;


  constructor(id: number, signature: string, output: string) {
    this.id = id;
    this.signature = signature;
    this.output = output;
  }
}
