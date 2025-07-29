package com.poc.analytics.repository;

import com.poc.analytics.entity.OrderAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderAnalyticsRepository extends JpaRepository<OrderAnalytics, Long> {
    
    List<OrderAnalytics> findByCustomerId(String customerId);
    
    List<OrderAnalytics> findByProductId(String productId);
    
    List<OrderAnalytics> findByDayBucket(String dayBucket);
    
    List<OrderAnalytics> findByHourBucket(String hourBucket);
    
    @Query("SELECT COUNT(o) FROM OrderAnalytics o WHERE o.orderTimestamp >= :startTime")
    Long countOrdersSince(@Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT SUM(o.orderValue) FROM OrderAnalytics o WHERE o.orderTimestamp >= :startTime")
    BigDecimal sumOrderValueSince(@Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT o.productId, COUNT(o), SUM(o.quantity), SUM(o.orderValue) " +
           "FROM OrderAnalytics o WHERE o.dayBucket = :dayBucket " +
           "GROUP BY o.productId ORDER BY COUNT(o) DESC")
    List<Object[]> getTopProductsByDay(@Param("dayBucket") String dayBucket);
    
    @Query("SELECT o.hourBucket, COUNT(o), SUM(o.orderValue) " +
           "FROM OrderAnalytics o WHERE o.dayBucket = :dayBucket " +
           "GROUP BY o.hourBucket ORDER BY o.hourBucket")
    List<Object[]> getHourlyStatsByDay(@Param("dayBucket") String dayBucket);
}