 // INI - 7
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  isSpinning: boolean = false;
  loginForm!: FormGroup;

  constructor(private fb:FormBuilder){}

  ngOnInit(){
    this.loginForm=this.fb.group({
      email:[null,[Validators.email, Validators.required]],
      password:[null,[Validators.required]]
    })
  }

  login(){
    console.log(this.loginForm.value);
    //INI-6
   
  }
}

// FIN - 7