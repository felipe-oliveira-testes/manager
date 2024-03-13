package com.manager.repository;

import com.manager.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order> findByItemIdAndStatusOrderByIdAsc(Long itemId, String status);
}
