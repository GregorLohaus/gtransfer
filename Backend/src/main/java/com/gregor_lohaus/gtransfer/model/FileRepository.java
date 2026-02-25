package com.gregor_lohaus.gtransfer.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, String> {

    @Query("SELECT f FROM File f WHERE f.expireyDateTime IS NOT NULL AND f.expireyDateTime < :now")
    List<File> findExpired(@Param("now") LocalDateTime now);
}
