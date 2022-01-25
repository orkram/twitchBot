import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from "../../environments/environment";


@Injectable()
export class NotificationService {

  constructor(private http: HttpClient) {}

  removeNotification(
    id: number,
    notification: string,
    frequency: number
  ): Observable<any> {
    return this.http.delete(environment.backend + `recurringNotification`, {
      body: {id, notification, frequency}
    });
  }

  addNotification(id: number,  notification: String, frequency: number): Observable<any> {
    return this.http.post(environment.backend + `recurringNotification`, {
      id: id,
      notification: notification,
      frequency: frequency
    });
  }

  getNotifications(): Observable<any> {
    return this.http.get(environment.backend + `recurringNotification`);
  }
}
