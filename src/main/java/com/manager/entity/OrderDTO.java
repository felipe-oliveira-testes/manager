package com.manager.entity;

import java.util.Date;
import java.util.List;

public class OrderDTO {
    private Long id;
    private Date creationDate;
    private Long itemId;
    private Long userId;
    private long quantity;
    private String status;
    private List<StockMovementDTO> stockMovementDTOList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<StockMovementDTO> getStockMovementDTOList() {
        return stockMovementDTOList;
    }

    public void setStockMovementDTOList(List<StockMovementDTO> stockMovementDTOList) {
        this.stockMovementDTOList = stockMovementDTOList;
    }
}
