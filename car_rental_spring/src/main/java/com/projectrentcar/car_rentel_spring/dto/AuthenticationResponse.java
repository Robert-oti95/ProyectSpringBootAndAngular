//INI-8
package com.projectrentcar.car_rentel_spring.dto;

import com.projectrentcar.car_rentel_spring.enums.UserRole;

import lombok.Data;
@Data
public class AuthenticationResponse {
    
    private String jwt;
    private UserRole userRole;
    private Long userId;
}
//FIN-8