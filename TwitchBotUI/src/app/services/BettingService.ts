import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';


@Injectable()
export class BettingService {

  constructor(private http: HttpClient) {
  }

  startBet(): Observable<any> {
    return this.http.post(`http://localhost:8080/startBet`, {});
  }

  getOngoingBet(): Observable<any> {
    return this.http.get(`http://localhost:8080/betState`)
  };


  finishBet( outcome: String): Observable<any> {
    return this.http.post(`http://localhost:8080/finishBet`, {
      outcome: outcome
    });
  }


}
