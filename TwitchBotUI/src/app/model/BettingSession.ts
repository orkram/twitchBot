export class BettingSession {
  id: number;
  state: string;
  winPool: string;
  losePool: string


  constructor(id: number, state: string, winPool: string, losePool: string) {
    this.id = id;
    this.state = state;
    this.winPool = winPool;
    this.losePool = losePool;
  }
}
