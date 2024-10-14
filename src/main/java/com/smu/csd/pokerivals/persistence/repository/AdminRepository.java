package com.smu.csd.pokerivals.persistence.repository;

import com.smu.csd.pokerivals.persistence.entity.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AdminRepository extends JpaRepository<Admin,String> {
    @Query("select a from Admin a where a.invitedBy.username = :username")
    List<Admin> findAdminsInvitedBy(@Param("username") String username);
}
