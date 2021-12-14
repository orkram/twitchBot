import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';


@Injectable()
export class CustomCommandService {

  constructor(private http: HttpClient) {}

  removeTerm(
    id: string,
    term: string,
  ): Observable<any> {
    return this.http.delete(`http://localhost:8080/`, {
      body: {id, term}
    });
  }

  addTerm(id: string, term: String): Observable<any> {
    return this.http.put(`http://localhost:8080/`, {
      id: id,
      term: term
    });
  }

  getTerms(username: string, name: string, participants: string[]): Observable<any> {
    console.log(participants);
    return this.http.get(`http://localhost:8080/`);
  }


}
