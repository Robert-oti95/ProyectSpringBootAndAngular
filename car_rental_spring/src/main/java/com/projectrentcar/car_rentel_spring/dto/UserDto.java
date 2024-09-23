package com.projectrentcar.car_rentel_spring.dto;

import com.projectrentcar.car_rentel_spring.enums.UserRole;

import lombok.Data;

//INICIO - VID04
@Data
public class UserDto {
    
    private Long id;
    private String name;
    private String email;
    private UserRole userRole;
}
//FIN - VID04
