package com.example.app.repository;

import com.example.app.model.ServicePassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicePasswordRepository extends JpaRepository<ServicePassword, Long> {
    List<ServicePassword> findByUsername(String username);
    
    void deleteByUsernameAndServiceName(String username, String serviceName);
    
    Optional<ServicePassword> findByUsernameAndServiceName(String username, String serviceName);
}
