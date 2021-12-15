export class SelectedWhitelistService{
  private selectedWhitelists: Array<string> = [];

  setSelectedWhitelists(users: Array<string>): void{
    this.selectedWhitelists = users;
  }

  getSelectedWhitelists(): Array<string>{
    return this.selectedWhitelists;
  }

  clear(): void{
    this.selectedWhitelists = [];
  }
}
