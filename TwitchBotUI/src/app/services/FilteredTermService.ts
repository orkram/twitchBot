import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from "../../environments/environment";


@Injectable()
export class FilteredTermService {

  constructor(private http: HttpClient) {}

  removeTerm(
    id: number,
    term: string,
  ): Observable<any> {
    return this.http.delete(environment.backend + `filteredTerms`, {
      body: {id, term}
    });
  }

  addTerm(id: number, term: String): Observable<any> {
    return this.http.post(environment.backend + `filteredTerms`, {
      id: id,
      term: term
    });
  }

  getTerms(): Observable<any> {

    return this.http.get(environment.backend + `filteredTerms`);
  }
}
