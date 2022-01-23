export class Notification {
  id: number;
  notification: string;
  frequency: number;


  constructor(id: number, text: string, periodicity: number) {
    this.id = id;
    this.notification = text;
    this.frequency = periodicity;
  }
}
