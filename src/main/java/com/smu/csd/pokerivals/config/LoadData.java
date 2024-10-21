package com.smu.csd.pokerivals.config;

import com.smu.csd.pokerivals.persistence.entity.user.Admin;
import com.smu.csd.pokerivals.persistence.entity.user.Player;
import com.smu.csd.pokerivals.persistence.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
class LoadData {
  
  @Bean
  CommandLineRunner initDatabase(UserRepository userRepository) {

    return args -> {
      userRepository.deleteById("joshua");
      log.info("Preloading " + userRepository.save(new Player("joshua","101754849930742817639")));
    };
  }
}