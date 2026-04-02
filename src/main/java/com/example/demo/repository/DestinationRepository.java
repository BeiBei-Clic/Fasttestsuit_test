package com.example.demo.repository;

import com.example.demo.model.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {
    List<Destination> findByLocationContaining(String location);
    List<Destination> findByRatingGreaterThanEqual(Double rating);
    List<Destination> findByOrderByVisitCountDesc();
}
