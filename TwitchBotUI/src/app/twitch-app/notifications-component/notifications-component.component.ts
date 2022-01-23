import {AfterViewInit, Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {SelectedNotificationService} from "../../services/SelectedNotificationService";
import {Notification} from "../../model/Notification";
import {NotificationService} from "../../services/NotificationService";

@Component({
  selector: 'app-notifications-component',
  templateUrl: './notifications-component.component.html',
  styleUrls: ['./notifications-component.component.scss']
})
export class NotificationsComponentComponent implements OnInit, AfterViewInit{

  constructor( private notificationsService: NotificationService, private selectedNotificationsService: SelectedNotificationService) { }

  notifications: Notification[] = []
  selectedNotifications: {[key: string]: boolean} = {};

  notificationForm: FormGroup = new FormGroup({
    notification: new FormControl('', Validators.compose([Validators.required])),
    frequency: new FormControl('', Validators.compose([Validators.required, Validators.pattern("^[0-9]*$"),]))
  });


  getSelectedNotifications(): Array<string>{
    return Object.keys(this.selectedNotifications).filter((name: string) => this.selectedNotifications[name]);
  }

  refreshNotifications() {
    this.notificationsService.getNotifications().subscribe(
      (terms: Notification[]) =>{
        console.log(terms)
        this.notifications = terms.map((x: Notification) => x)
        this.selectedNotifications = {}
      }
    )

  }

  ngOnInit(): void {
  }

  ngAfterViewInit():void{
    this.refreshNotifications()
  }

  submitNotification(formDirective: any): void{
    this.notificationsService
      .addNotification(
        1,
        this.notificationForm.value.notification,
        this.notificationForm.value.frequency
      )
      .subscribe(
        _ => {},
        _ => {},
        () => {
          this.refreshNotifications()
          formDirective.resetForm();
        }
      )
  }

  removeNotifications(): void {
    this.selectedNotificationsService
      .getSelectedNotifications()
      .map( (filter: any) => {
          console.log(filter)
          let notif = this.notifications.find((t) => t.notification == filter)

          this.notificationsService.removeNotification(notif!.id, notif!.notification, notif!.frequency).subscribe(
            _ => {
            },
            _ => {
            },
            () => {
              window.location.reload(); //ugly, should be unnecessary
            }
          )
        }
      );
  }

  unselect(): void{
    this.selectedNotificationsService.clear();
    this.selectedNotifications = {};
  }

  onSelect(selected: any, term: any): void{
    this.selectedNotifications[term.notification] = selected;
    this.selectedNotificationsService.setSelectedNotifications(this.getSelectedNotifications()) ;
  }
}
