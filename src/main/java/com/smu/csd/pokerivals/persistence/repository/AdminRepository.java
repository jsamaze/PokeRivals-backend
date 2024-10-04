package com.smu.csd.pokerivals.persistence.repository;

import com.smu.csd.pokerivals.persistence.entity.user.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface AdminRepository extends JpaRepository<Admin,String> {
}
