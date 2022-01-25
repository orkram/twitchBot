import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from "../../environments/environment";


@Injectable()
export class UserCommandService {

  constructor(private http: HttpClient) {}

  removeCommands(
    id: number,
    signature: string,
    output: String
  ): Observable<any> {
    return this.http.delete(environment.backend + `customCommand`, {
      body: {id, signature, output}
    });
  }

  addCommand(id: number,  signature: String, output: String): Observable<any> {
    return this.http.post(environment.backend + `customCommand`, {
      id: id,
      signature: signature,
      output: output
    });
  }

  getCommands(): Observable<any> {
    return this.http.get(environment.backend + `customCommand`);
  }
}
