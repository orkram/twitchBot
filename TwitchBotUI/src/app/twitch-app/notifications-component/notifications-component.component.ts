import {AfterViewInit, Component, OnInit} from '@angular/core';
import {FilteredTermService} from "../../services/FilteredTermService";
import {FilteredTerm} from "../../model/FilteredTerm";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {SelectedNotificationService} from "../../services/SelectedNotificationService";

@Component({
  selector: 'app-notifications-component',
  templateUrl: './notifications-component.component.html',
  styleUrls: ['./notifications-component.component.scss']
})
export class NotificationsComponentComponent implements OnInit, AfterViewInit{

  constructor( private filteredTermService: FilteredTermService, private selectedNotificationsService: SelectedNotificationService) { }

  notifications: FilteredTerm[] = []
  selectedNotifications: {[key: string]: boolean} = {};

  notificationForm: FormGroup = new FormGroup({
    notification: new FormControl('', Validators.compose([Validators.required])),
  });

  getSelectedNotifications(): Array<string>{
    return Object.keys(this.selectedNotifications).filter((name: string) => this.selectedNotifications[name]);
  }

  refreshNotifications() {
    this.filteredTermService.getTerms().subscribe(
      (terms: FilteredTerm[]) =>{
        console.log(terms)
        this.notifications = terms.map((x: FilteredTerm) => x)
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
    console.log(this.notificationForm.value.term)
    this.filteredTermService
      .addTerm(
        1,
        this.notificationForm.value.term
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
          let term = this.notifications.find((t) => t.term == filter)
          this.filteredTermService.removeTerm(term!.id, term!.term).subscribe(
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
    console.log(selected)
    console.log(term)

    this.selectedNotifications[term.term] = selected;
    this.selectedNotificationsService.setSelectedNotifications(this.getSelectedNotifications()) ;
  }
}
