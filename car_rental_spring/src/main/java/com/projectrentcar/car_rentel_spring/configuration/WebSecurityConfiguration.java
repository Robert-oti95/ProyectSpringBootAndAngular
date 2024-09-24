//INI-8
package com.projectrentcar.car_rentel_spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.projectrentcar.car_rentel_spring.enums.UserRole;
import com.projectrentcar.car_rentel_spring.service.jwt.UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity //Habilita la seguridad web en la aplicación. Spring Security interceptará todas las solicitudes HTTP y aplicará las configuraciones de seguridad.
@EnableMethodSecurity //Permite la seguridad basada en anotaciones en métodos, como @PreAuthorize.
@RequiredArgsConstructor
public class WebSecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    //Beans: Los beans son métodos anotados con @Bean, lo que indica a Spring que los instancie y gestione en su contenedor de inversión de control.
    //securityFilterChain(HttpSecurity http): Este bean define la cadena de filtros de seguridad que Spring aplicará a las solicitudes HTTP. Configura la seguridad de la aplicación.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        
        //"http.csrf(AbstractHttpConfigurer::disable)" ->Deshabilitar CSRF: Se desactiva la protección CSRF (Cross-Site Request Forgery), que es innecesaria para aplicaciones que usan JWT.
        http.csrf(AbstractHttpConfigurer::disable).
        //Configurar autorizaciones: Define las rutas y los permisos. 
        //Las rutas bajo /api/auth/** se permiten para todos los usuarios sin autenticación. 
        //Las rutas bajo /api/admin/** y /api/customer/** requieren que el usuario tenga los roles ADMIN o CUSTOMER, respectivamente. 
        //Cualquier otra ruta requiere autenticación.
        authorizeHttpRequests(
            request -> request.requestMatchers("/api/auth/**").permitAll()
                             .requestMatchers("/api/admin/**").hasAnyAuthority(UserRole.ADMIN.name())
                             .requestMatchers("/api/customer/**").hasAnyAuthority(UserRole.CUSTOMER.name())
                             .anyRequest().authenticated()).
                             //Establecer el tipo de sesión: Se establece una política de sesión sin estado (SessionCreationPolicy.STATELESS), 
                             //que es estándar para autenticación con JWT, ya que no se mantiene estado del usuario en el servidor.
                             sessionManagement(
                                manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                                  //Proveedores de autenticación: Se configura el proveedor de autenticación personalizado y el filtro JWT (JwtAuthenticationFilter) 
                                                  //para procesar las solicitudes antes de que se maneje la autenticación de nombre de usuario y contraseña.
                                                  .authenticationProvider(authenticationProvider())
                                                  .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    //passwordEncoder(): Este bean define un codificador de contraseñas utilizando BCrypt, 
    //un algoritmo de hashing para almacenar las contraseñas de manera segura.
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    //authenticationProvider(): Define el proveedor de autenticación que se encarga de autenticar a los usuarios. 
    //Usa un DaoAuthenticationProvider que interactúa con la base de datos para obtener los detalles del usuario.
    //  * Se establece el servicio de detalles del usuario (UserService) y se utiliza el codificador de contraseñas antes definido.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    //authenticationManager(): Este bean devuelve el AuthenticationManager, que es responsable de autenticar las credenciales del usuario (como nombre de usuario y contraseña).
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

}
//INI-8
