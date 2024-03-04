package com.albert.springbootessentials2.repository;

import com.albert.springbootessentials2.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    @Query("""
        select au from AppUser au where au.username = :username
        """)
    AppUser findByUsername(String username);
}
