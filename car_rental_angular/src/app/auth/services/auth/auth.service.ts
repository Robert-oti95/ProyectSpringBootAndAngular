import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
//INI-6
const BASE_URL = ["http://localhost:8080/"]
//FIN-6
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  //INI-6
  constructor(private http: HttpClient) { }

  register(signupRequest: any): Observable<any> {
    return this.http.post(BASE_URL + "api/auth/signup", signupRequest);  //AuthController:@RequestMapping("/api/auth") && @PostMapping("/signup")
  }
  //FIN-6
}
