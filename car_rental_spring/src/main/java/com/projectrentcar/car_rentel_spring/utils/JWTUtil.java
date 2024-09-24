//INI-8
package com.projectrentcar.car_rentel_spring.utils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil { 
    //Extrae el nombre de usuario del token. 
    //Para esto, llama al método extractClaim que obtiene el "claim" correspondiente al sujeto (subject) del token, 
    //que normalmente es el nombre de usuario.
    public String extractUserName(String token){
        return extractClaim(token,Claims::getSubject);
    }
    //Genera un token JWT para un usuario,sin ningún "claim" extra. 
    //Llama a generateToken con un HashMap vacío y el objeto UserDetails, 
    //que contiene información del usuario autenticado.
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }
    //Valida si el token es correcto. 
    //Primero, extrae el nombre de usuario del token y lo compara con el nombre de usuario en el objeto UserDetails. 
    //Además, verifica si el token no ha expirado.
    public boolean isTokenValid(String token,UserDetails userDetails){
        final String userName = extractUserName(token);
        return(userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    //Extrae un "claim" específico del token, 
    //utilizando una función que pasa como parámetro (claimsResolver). 
    //Un "claim" es una parte del token que contiene datos (como el nombre de usuario, fecha de expiración, etc.).
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims= extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    //Genera un token JWT con "claims" adicionales (extraClaims). 
    //Establece el asunto (subject) como el nombre de usuario, la fecha de emisión y la fecha de expiración del token. 
    //Firma el token con una clave secreta y el algoritmo de firma HS256.
    public String generateToken(Map<String,Object> extraClaims,UserDetails userDetails){
        return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
    }
    //Similar al método anterior, pero genera un "refresh token", que normalmente tiene una fecha de expiración más larga. 
    //Este token se utiliza para obtener un nuevo token JWT sin volver a autenticar al usuario.
    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 604800000))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }
    //Verifica si el token ha expirado. 
    //Compara la fecha de expiración del token con la fecha actual.
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    //Extrae la fecha de expiración del token, usando el método extractClaim.
    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }
    //Extrae todos los "claims" del token. El token se parsea usando la clave de firma (signing key).
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }
    //Devuelve la clave secreta utilizada para firmar el token. 
    //La clave se decodifica de una cadena en formato Base64.
    private Key getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode("413F4428472B4B6250655368566D5970337336763979244226452948404D6351");
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
//FIN-8