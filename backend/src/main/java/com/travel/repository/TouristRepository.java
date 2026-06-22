package com.travel.repository;

import com.travel.entity.Tourist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TouristRepository extends JpaRepository<Tourist, Long> {

    Optional<Tourist> findByPhone(String phone);

    Optional<Tourist> findByEmail(String email);
}
