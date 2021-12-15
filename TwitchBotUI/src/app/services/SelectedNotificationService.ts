export class SelectedNotificationService{
  private selectedNotifications: Array<string> = [];

  setSelectedNotifications(users: Array<string>): void{
    this.selectedNotifications = users;
  }

  getSelectedNotifications(): Array<string>{
    return this.selectedNotifications;
  }

  clear(): void{
    this.selectedNotifications = [];
  }
}
