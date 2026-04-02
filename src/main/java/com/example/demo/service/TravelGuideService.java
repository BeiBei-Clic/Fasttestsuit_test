package com.example.demo.service;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Destination;
import com.example.demo.model.TravelGuide;
import com.example.demo.repository.TravelGuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 旅行攻略服务类
 * 提供攻略的CRUD操作和业务逻辑
 */
@Service
public class TravelGuideService {

    @Autowired
    private TravelGuideRepository travelGuideRepository;

    @Autowired
    private DestinationService destinationService;

    /**
     * 获取所有攻略
     */
    public List<TravelGuide> getAllGuides() {
        return travelGuideRepository.findAll();
    }

    /**
     * 根据ID获取攻略
     */
    public TravelGuide getGuideById(Long id) {
        return travelGuideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("攻略", "id", id));
    }

    /**
     * 获取某个目的地的所有攻略
     */
    public List<TravelGuide> getGuidesByDestination(Long destinationId) {
        // 验证目的地存在
        destinationService.getDestinationById(destinationId);
        return travelGuideRepository.findByDestinationId(destinationId);
    }

    /**
     * 搜索攻略（按标题关键字）
     */
    public List<TravelGuide> searchGuides(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键字不能为空");
        }
        return travelGuideRepository.findByTitleContaining(keyword);
    }

    /**
     * 获取热门攻略
     */
    public List<TravelGuide> getPopularGuides() {
        return travelGuideRepository.findByOrderByViewsDesc();
    }

    /**
     * 创建攻略
     */
    @Transactional
    public TravelGuide createGuide(TravelGuide guide, Long destinationId) {
        if (guide.getTitle() == null || guide.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("攻略标题不能为空");
        }

        if (guide.getContent() != null && guide.getContent().length() > 5000) {
            throw new BusinessException("内容过长", "攻略内容不能超过5000字符");
        }

        if (destinationId != null) {
            Destination destination = destinationService.getDestinationById(destinationId);
            guide.setDestination(destination);
        }

        return travelGuideRepository.save(guide);
    }

    /**
     * 更新攻略
     */
    @Transactional
    public TravelGuide updateGuide(Long id, TravelGuide guideDetails) {
        TravelGuide guide = getGuideById(id);

        if (guideDetails.getTitle() != null) {
            guide.setTitle(guideDetails.getTitle());
        }
        if (guideDetails.getContent() != null) {
            if (guideDetails.getContent().length() > 5000) {
                throw new BusinessException("内容过长", "攻略内容不能超过5000字符");
            }
            guide.setContent(guideDetails.getContent());
        }
        if (guideDetails.getTravelDuration() != null) {
            guide.setTravelDuration(guideDetails.getTravelDuration());
        }
        if (guideDetails.getEstimatedBudget() != null) {
            guide.setEstimatedBudget(guideDetails.getEstimatedBudget());
        }
        if (guideDetails.getBestSeason() != null) {
            guide.setBestSeason(guideDetails.getBestSeason());
        }

        return travelGuideRepository.save(guide);
    }

    /**
     * 删除攻略
     */
    @Transactional
    public void deleteGuide(Long id) {
        TravelGuide guide = getGuideById(id);
        travelGuideRepository.delete(guide);
    }

    /**
     * 记录浏览
     */
    @Transactional
    public void recordView(Long id) {
        TravelGuide guide = getGuideById(id);
        guide.incrementViews();
        travelGuideRepository.save(guide);
    }

    /**
     * 点赞
     */
    @Transactional
    public void likeGuide(Long id) {
        TravelGuide guide = getGuideById(id);
        guide.incrementLikes();
        travelGuideRepository.save(guide);
    }
}
