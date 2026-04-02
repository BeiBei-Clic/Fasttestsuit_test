package com.example.demo.repository;

import com.example.demo.model.TravelGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelGuideRepository extends JpaRepository<TravelGuide, Long> {
    List<TravelGuide> findByDestinationId(Long destinationId);
    List<TravelGuide> findByAuthor(String author);
    List<TravelGuide> findByTitleContaining(String keyword);
    List<TravelGuide> findByOrderByViewsDesc();
}
