import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';


@Injectable()
export class UserCommandService {

  constructor(private http: HttpClient) {}

  removeCommands(
    id: number,
    signature: string,
    output: String
  ): Observable<any> {
    return this.http.delete(`http://localhost:8080/customCommand`, {
      body: {id, signature, output}
    });
  }

  addCommand(id: number,  signature: String, output: String): Observable<any> {
    return this.http.post(`http://localhost:8080/customCommand`, {
      id: id,
      signature: signature,
      output: output
    });
  }

  getCommands(): Observable<any> {
    return this.http.get(`http://localhost:8080/customCommand`);
  }
}
