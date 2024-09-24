//INI-8
package com.projectrentcar.car_rentel_spring.service.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projectrentcar.car_rentel_spring.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {

            //* Busca un usuario en la base de datos utilizando el método findFirstByEmail() del UserRepository, 
            //  el cual busca un usuario por su dirección de correo electrónico (en este caso, el nombre de usuario es un correo).
            //* Si encuentra un usuario, lo devuelve como un objeto UserDetails.
            //* Si no lo encuentra, lanza una excepción UsernameNotFoundException, 
            //  que es la forma en que Spring Security gestiona la falta de coincidencias de usuarios.
            @Override
            public UserDetails loadUserByUsername(String username){
                return userRepository.findFirstByEmail(username)
                                     .orElseThrow(()-> new UsernameNotFoundException("User not found"));
            }         
        };
    }
    
}
//FIN-8
