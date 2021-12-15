export class SelectedTermsService{
  private selectedTerms: Array<string> = [];

  setSelectedTerms(users: Array<string>): void{
    this.selectedTerms = users;
  }

  getSelectedTerms(): Array<string>{
    return this.selectedTerms;
  }

  clear(): void{
    this.selectedTerms = [];
  }
}
