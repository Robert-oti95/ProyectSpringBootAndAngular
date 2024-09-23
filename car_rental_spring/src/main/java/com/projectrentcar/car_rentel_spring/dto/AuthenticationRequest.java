//INI-8
package com.projectrentcar.car_rentel_spring.dto;

import lombok.Data;

@Data
public class AuthenticationRequest {
    
    private String email;
    private String password;
}
//FIN-8