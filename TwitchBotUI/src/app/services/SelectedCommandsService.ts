export class SelectedCommandsService{
  private selectedCommands: Array<string> = [];

  setSelectedCommands(cmds: Array<string>): void{
    this.selectedCommands = cmds;
  }

  getSelectedCommands(): Array<string>{
    return this.selectedCommands;
  }

  clear(): void{
    this.selectedCommands = [];
  }
}
