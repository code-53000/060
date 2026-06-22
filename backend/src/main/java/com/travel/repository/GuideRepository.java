package com.travel.repository;

import com.travel.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {

    List<Guide> findByActiveTrue();

    @Query("SELECT DISTINCT g FROM Guide g JOIN g.languages l WHERE l IN :languages AND g.active = true")
    List<Guide> findByLanguagesIn(@Param("languages") Set<String> languages);
}
