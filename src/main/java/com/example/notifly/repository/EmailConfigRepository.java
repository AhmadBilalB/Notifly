package com.example.notifly.repository;

import com.example.notifly.entity.EmailConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailConfigRepository extends JpaRepository<EmailConfig, Long> {
    Optional<EmailConfig> findFirstByOrderByIdAsc(); // Fetch the first config
}
