//INI - 5
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth/auth.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss'
})
export class SignupComponent {

  isSpinning: boolean = false;
  signupForm!: FormGroup;

  constructor(private fb: FormBuilder
    //INI-6
    , private authService: AuthService
    , private message: NzMessageService
    , private router: Router) { }
  //FIN-6
  ngOnInit() {

    this.signupForm = this.fb.group({
      name: [null, [Validators.required]],
      email: [null, [Validators.required, Validators.email]],
      password: [null, [Validators.required]],
      checkPassword: [null, [Validators.required, this.confirmationValidate]]
    })
  }

  confirmationValidate = (control: FormGroup): { [s: string]: boolean } => {
    if (!control.value) {
      return { require: true };
    } else if (control.value !== this.signupForm.controls['password'].value) {
      return { confirm: true, error: true };
    }
    return {};
  }


  register() {

    console.log(this.signupForm.value);
    //INI-6
    this.authService.register(this.signupForm.value).subscribe(
      (res) => {
        console.log(res);
        if (res.id != null) {
          this.message.success("Signup successful", { nzDuration: 5000 });
          this.router.navigateByUrl("/login");
        } else {
          this.message.error("Something went wrong", { nzDuration: 5000 });
        }
      })
    //FIN-6
  }


}
//INI -5