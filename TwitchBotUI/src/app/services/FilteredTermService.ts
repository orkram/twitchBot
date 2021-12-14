import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';


@Injectable()
export class FilteredTermService {

  constructor(private http: HttpClient) {}

  removeTerm(
    id: number,
    term: string,
  ): Observable<any> {
    return this.http.delete(`http://localhost:8080/filteredTerms`, {
      body: {id, term}
    });
  }

  addTerm(id: number, term: String): Observable<any> {
    return this.http.post(`http://localhost:8080/filteredTerms`, {
      id: id,
      term: term
    });
  }

  getTerms(): Observable<any> {

    return this.http.get(`http://localhost:8080/filteredTerms`);
  }
}
