package com.smu.csd.pokerivals.persistence.repository;

import com.smu.csd.pokerivals.persistence.entity.user.Clan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClanRepository extends JpaRepository<Clan,String> {}