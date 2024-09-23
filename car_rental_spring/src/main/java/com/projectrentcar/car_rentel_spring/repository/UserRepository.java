package com.projectrentcar.car_rentel_spring.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectrentcar.car_rentel_spring.entity.User;
//INICIO - VID04
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findFirstByEmail(String email);

}
//FIN - VID04
