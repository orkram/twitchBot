import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';


@Injectable()
export class NotificationService {

  constructor(private http: HttpClient) {}

  removeNotification(
    id: number,
    notification: string,
    frequency: number
  ): Observable<any> {
    return this.http.delete(`http://localhost:8080/recurringNotification`, {
      body: {id, notification, frequency}
    });
  }

  addNotification(id: number,  notification: String, frequency: number): Observable<any> {
    return this.http.post(`http://localhost:8080/recurringNotification`, {
      id: id,
      notification: notification,
      frequency: frequency
    });
  }

  getNotifications(): Observable<any> {
    return this.http.get(`http://localhost:8080/recurringNotification`);
  }
}
