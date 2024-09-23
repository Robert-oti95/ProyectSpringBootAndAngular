package com.projectrentcar.car_rentel_spring.service.auth;

import com.projectrentcar.car_rentel_spring.dto.SignupRequest;
import com.projectrentcar.car_rentel_spring.dto.UserDto;
//INICIO - VID04
public interface AuthService {
    
    UserDto createCustomer(SignupRequest signupRequest);

    boolean hasCustomerWithEmail(String email);
}
//FIN - VID04