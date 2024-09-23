package com.projectrentcar.car_rentel_spring.service.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.projectrentcar.car_rentel_spring.dto.SignupRequest;
import com.projectrentcar.car_rentel_spring.dto.UserDto;
import com.projectrentcar.car_rentel_spring.entity.User;
import com.projectrentcar.car_rentel_spring.enums.UserRole;
import com.projectrentcar.car_rentel_spring.repository.UserRepository;

import lombok.RequiredArgsConstructor;

//INICIO - VID04
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;

    @Override
    public UserDto createCustomer(SignupRequest signupRequest) {

        User user = new User();
        user.setName(signupRequest.getName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(
            //INI-8 
            new BCryptPasswordEncoder().encode(signupRequest.getPassword())
            //FIN-8
        );
        user.setUserRole(UserRole.CUSTOMER);
        User createdUser = userRepository.save(user);
        UserDto userDto = new UserDto();
        userDto.setId(createdUser.getId());
        return userDto;
    }

    @Override
    public boolean hasCustomerWithEmail(String email) {

        return userRepository.findFirstByEmail(email).isPresent();
    }
}
//FIN - VID04