package com.mitanshu.spring_ecomm.repositories;

import com.mitanshu.spring_ecomm.entities.AppRole;
import com.mitanshu.spring_ecomm.entities.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(AppRole appRole);
}
