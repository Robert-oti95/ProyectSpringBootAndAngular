//INI-8
package com.projectrentcar.car_rentel_spring.configuration;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.projectrentcar.car_rentel_spring.service.jwt.UserService;
import com.projectrentcar.car_rentel_spring.utils.JWTUtil;
import org.apache.commons.lang3.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
//OncePerRequestFilter: lo que significa que el filtro se ejecutará una vez por cada solicitud.
//Su principal tarea es interceptar cada solicitud HTTP, comprobar si hay un token JWT en el encabezado de autorización, validarlo y establecer la autenticación en el contexto de seguridad si es válido.
public class JwtAuthenticationFilter extends OncePerRequestFilter{ 

    private final JWTUtil jwtUtil;
    private final UserService userService;

    //doFilterInternal(): Este es el núcleo del filtro. 
    //Intercepta cada solicitud, realiza las comprobaciones necesarias y delega la solicitud a la siguiente entidad en la cadena del filtro.
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
                //Extraer el JWT del encabezado de autorización:
                //El filtro comprueba si el encabezado Authorization está presente y si comienza con "Bearer" (la convención estándar para tokens JWT).
                //Si el encabezado no está presente o no contiene un JWT válido, la solicitud continúa sin aplicar autenticación.
                final String authHeader = request.getHeader("Authorization");
                final String jwt;
                final String userEmail;
                
                if(StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader,"Bearer")){
                    filterChain.doFilter(request, response);
                    return;
                }
                jwt = authHeader.substring(7);
                //Extraer el nombre de usuario del token:
                //Una vez que se ha extraído el token JWT, se usa jwtUtil.extractUserName() para extraer el nombre de usuario (correo electrónico) del token.
                //Si se obtiene un nombre de usuario y no hay autenticación previa en el contexto de seguridad (es decir, el usuario aún no está autenticado en esta solicitud), continúa el proceso.
                userEmail = jwtUtil.extractUserName(jwt);
                if(StringUtils.isNotEmpty(userEmail) && SecurityContextHolder.getContext().getAuthentication() == null){
                    //Cargar los detalles del usuario y validar el token:
                    //Utiliza userService para cargar los detalles del usuario desde la base de datos utilizando el nombre de usuario extraído del token.
                    //Después, usa jwtUtil.isTokenValid() para validar si el token JWT coincide con los detalles del usuario.
                    UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userEmail);
                    if (jwtUtil.isTokenValid(jwt, userDetails)) {
                        //Configurar el contexto de seguridad:
                        //Si el token es válido, se crea un nuevo contexto de seguridad (SecurityContext) y se crea un token de autenticación UsernamePasswordAuthenticationToken con los detalles del usuario.
                        //Este token se establece como autenticación en el SecurityContextHolder.
                        SecurityContext context =  SecurityContextHolder.createEmptyContext();
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        context.setAuthentication(authToken);
                        SecurityContextHolder.setContext(context);
                    }
                }
                //Continuar con la cadena de filtros:
                //Una vez que el filtro ha completado su tarea, llama a filterChain.doFilter() para continuar con el siguiente filtro o procesador en la cadena.
                filterChain.doFilter(request, response);
            }
    
}
//FIN-8