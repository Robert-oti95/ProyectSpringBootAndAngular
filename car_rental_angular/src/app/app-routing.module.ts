import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SignupComponent } from './auth/components/signup/signup.component';
import { LoginComponent } from './auth/components/login/login.component';

const routes: Routes = [
  {path:"register",component:SignupComponent}, //http://localhost:4200/register
  {path:"login",component:LoginComponent}     //http://localhost:4200/login
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
