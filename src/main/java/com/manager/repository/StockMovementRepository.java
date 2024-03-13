package com.manager.repository;

import com.manager.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    public List<StockMovement> findAllByItemId(Long itemId);

    public List<StockMovement> findAllByItemIdAndStatusIsNot(Long itemId, String status);
}
