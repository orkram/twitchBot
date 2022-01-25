import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {WhiteList} from "../model/WhiteList";
import {environment} from "../../environments/environment";


@Injectable()
export class WhiteListService {

  constructor(private http: HttpClient) {}

  headers = new HttpHeaders({
    'Content-Type': 'application/json',
    'Access-Control-Allow-Methods': 'GET',
    'Access-Control-Allow-Headers': 'Content-Type',
    'Access-Control-Allow-Origin': 'localhost:8080/whiteList',
    Anonymous: ''
  });

  removeTerm(
    whiteListTerm: WhiteList
  ): Observable<any> {
    return this.http.delete(environment.backend + `whiteList`, {
      body: whiteListTerm
    });
  }

  addTerm(term: String): Observable<any> {
    return this.http.post(environment.backend + `whiteList`, {
      id: 1,
      allowedDomain: term
    });
  }

  getTerms(): Observable<any> {
    return this.http.get(environment.backend + `whiteList`);
  }


}

