//INICIO - 4
package com.projectrentcar.car_rentel_spring.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectrentcar.car_rentel_spring.dto.AuthenticationRequest;
import com.projectrentcar.car_rentel_spring.dto.AuthenticationResponse;
import com.projectrentcar.car_rentel_spring.dto.SignupRequest;
import com.projectrentcar.car_rentel_spring.dto.UserDto;
import com.projectrentcar.car_rentel_spring.entity.User;
import com.projectrentcar.car_rentel_spring.repository.UserRepository;
import com.projectrentcar.car_rentel_spring.service.auth.AuthService;
import com.projectrentcar.car_rentel_spring.service.jwt.UserService;
import com.projectrentcar.car_rentel_spring.utils.JWTUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    //INI-8
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    //FIN-8

    @PostMapping("/signup")
    public ResponseEntity<?> signupCustomer(@RequestBody SignupRequest signupRequest){
        if(authService.hasCustomerWithEmail(signupRequest.getEmail()))
            return new ResponseEntity<>("Customer alredy exist with this email",HttpStatus.NOT_ACCEPTABLE);

        UserDto createdCustomerDto = authService.createCustomer(signupRequest);
        if(createdCustomerDto == null) return new ResponseEntity<>
            ("Customer no created, Come again later", HttpStatus.BAD_REQUEST);
        
        return new ResponseEntity<>(createdCustomerDto,HttpStatus.CREATED) ;
    }

    //INI-8
    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) 
        throws BadCredentialsException, DisabledException, UsernameNotFoundException{
        
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(), 
                authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password.");
        }        
        final UserDetails userDetails = userService.userDetailsService().loadUserByUsername(authenticationRequest.getEmail());
        Optional<User> optionalUser = userRepository.findFirstByEmail(userDetails.getUsername());
        final String  jwt = jwtUtil.generateToken(userDetails);
        AuthenticationResponse authenticationResponse =new AuthenticationResponse();
        if(optionalUser.isPresent()){
            authenticationResponse.setJwt(jwt);
            authenticationResponse.setUserId(optionalUser.get().getId());
            authenticationResponse.setUserRole(optionalUser.get().getUserRole());
        }
        return authenticationResponse;
    }
    //INI-8
}
//FIN - 4