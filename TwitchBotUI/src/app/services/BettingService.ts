import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from "../../environments/environment";


@Injectable()
export class BettingService {

  constructor(private http: HttpClient) {
  }

  startBet(): Observable<any> {
    return this.http.post(environment.backend + `startBet`, {});
  }

  getOngoingBet(): Observable<any> {
    return this.http.get(environment.backend + `betState`)
  };


  finishBet( outcome: String): Observable<any> {
    return this.http.post(environment.backend + `finishBet`, {
      outcome: outcome
    });
  }


}
