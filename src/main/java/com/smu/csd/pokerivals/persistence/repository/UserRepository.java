package com.smu.csd.pokerivals.persistence.repository;

import com.smu.csd.pokerivals.persistence.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findOneByGoogleSub(String googleSub);
}
