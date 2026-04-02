package com.example.demo.config;

import com.example.demo.model.Destination;
import com.example.demo.model.TravelGuide;
import com.example.demo.model.User;
import com.example.demo.repository.DestinationRepository;
import com.example.demo.repository.TravelGuideRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据初始化配置类
 * 应用启动时初始化一些测试数据
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            DestinationRepository destinationRepository,
            TravelGuideRepository guideRepository) {

        return args -> {
            // 初始化用户
            if (userRepository.count() == 0) {
                User user1 = new User("testuser", "123456", "test@example.com");
                user1.setNickname("测试用户");
                userRepository.save(user1);

                User user2 = new User("admin", "admin123", "admin@example.com");
                user2.setNickname("管理员");
                userRepository.save(user2);

                System.out.println("初始化用户数据完成");
            }

            // 初始化目的地
            if (destinationRepository.count() == 0) {
                Destination d1 = new Destination("北京", "中国首都，历史悠久", "中国");
                d1.setRating(4.8);
                d1.setImageUrl("/images/beijing.jpg");
                destinationRepository.save(d1);

                Destination d2 = new Destination("上海", "国际大都市", "中国");
                d2.setRating(4.6);
                d2.setImageUrl("/images/shanghai.jpg");
                destinationRepository.save(d2);

                Destination d3 = new Destination("广州", "美食之都", "中国");
                d3.setRating(4.5);
                d3.setImageUrl("/images/guangzhou.jpg");
                destinationRepository.save(d3);

                Destination d4 = new Destination("成都", "天府之国", "中国");
                d4.setRating(4.7);
                d4.setImageUrl("/images/chengdu.jpg");
                destinationRepository.save(d4);

                Destination d5 = new Destination("杭州", "人间天堂", "中国");
                d5.setRating(4.9);
                d5.setImageUrl("/images/hangzhou.jpg");
                destinationRepository.save(d5);

                System.out.println("初始化目的地数据完成");
            }

            // 初始化攻略
            if (guideRepository.count() == 0) {
                TravelGuide g1 = new TravelGuide(
                        "北京三日游攻略",
                        "第一天：故宫-天安门广场-王府井\n第二天：长城-十三陵\n第三天：颐和园-圆明园-鸟巢",
                        "旅游达人"
                );
                g1.setTravelDuration("3天");
                g1.setEstimatedBudget(3000.0);
                g1.setBestSeason("春秋");

                Destination beijing = destinationRepository.findAll().stream()
                        .filter(d -> d.getName().equals("北京"))
                        .findFirst()
                        .orElse(null);
                if (beijing != null) {
                    g1.setDestination(beijing);
                }
                guideRepository.save(g1);

                TravelGuide g2 = new TravelGuide(
                        "成都美食之旅",
                        "必吃美食：火锅、串串香、龙抄手、担担面\n推荐店铺：蜀大侠、小龙坎、陈麻婆豆腐",
                        "吃货小王"
                );
                g2.setTravelDuration("2天");
                g2.setEstimatedBudget(1500.0);
                g2.setBestSeason("四季皆宜");

                Destination chengdu = destinationRepository.findAll().stream()
                        .filter(d -> d.getName().equals("成都"))
                        .findFirst()
                        .orElse(null);
                if (chengdu != null) {
                    g2.setDestination(chengdu);
                }
                guideRepository.save(g2);

                System.out.println("初始化攻略数据完成");
            }
        };
    }
}
