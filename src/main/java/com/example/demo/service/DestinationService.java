package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Destination;
import com.example.demo.repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 目的地服务类
 * 提供目的地的CRUD操作
 */
@Service
public class DestinationService {

    @Autowired
    private DestinationRepository destinationRepository;

    /**
     * 获取所有目的地
     */
    public List<Destination> getAllDestinations() {
        return destinationRepository.findAll();
    }

    /**
     * 根据ID获取目的地
     */
    public Destination getDestinationById(Long id) {
        return destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("目的地", "id", id));
    }

    /**
     * 搜索目的地（按位置）
     */
    public List<Destination> searchByLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索位置不能为空");
        }
        return destinationRepository.findByLocationContaining(location);
    }

    /**
     * 获取高评分目的地
     */
    public List<Destination> getHighRatedDestinations(Double minRating) {
        if (minRating == null || minRating < 0 || minRating > 5) {
            throw new IllegalArgumentException("评分必须在0-5之间");
        }
        return destinationRepository.findByRatingGreaterThanEqual(minRating);
    }

    /**
     * 获取热门目的地
     */
    public List<Destination> getPopularDestinations() {
        return destinationRepository.findByOrderByVisitCountDesc();
    }

    /**
     * 创建目的地
     */
    @Transactional
    public Destination createDestination(Destination destination) {
        if (destination.getName() == null || destination.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("目的地名称不能为空");
        }
        return destinationRepository.save(destination);
    }

    /**
     * 更新目的地
     */
    @Transactional
    public Destination updateDestination(Long id, Destination destinationDetails) {
        Destination destination = getDestinationById(id);

        if (destinationDetails.getName() != null) {
            destination.setName(destinationDetails.getName());
        }
        if (destinationDetails.getDescription() != null) {
            destination.setDescription(destinationDetails.getDescription());
        }
        if (destinationDetails.getLocation() != null) {
            destination.setLocation(destinationDetails.getLocation());
        }
        if (destinationDetails.getImageUrl() != null) {
            destination.setImageUrl(destinationDetails.getImageUrl());
        }
        if (destinationDetails.getRating() != null) {
            destination.setRating(destinationDetails.getRating());
        }

        return destinationRepository.save(destination);
    }

    /**
     * 删除目的地
     */
    @Transactional
    public void deleteDestination(Long id) {
        Destination destination = getDestinationById(id);
        destinationRepository.delete(destination);
    }

    /**
     * 记录访问次数
     */
    @Transactional
    public void recordVisit(Long id) {
        Destination destination = getDestinationById(id);
        destination.incrementVisitCount();
        destinationRepository.save(destination);
    }
}
