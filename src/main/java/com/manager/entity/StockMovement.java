package com.manager.entity;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name="stock_movements")
public class StockMovement {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private Date creationDate;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;
    private long quantity;
    private String status;
    private long quantityAvailable;

    @ManyToMany(mappedBy = "stockMovements")
    private List<Order> orderList;


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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
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

    public long getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(long quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public static void merge(StockMovement stockMovement, StockMovementDTO dto) {
        if (stockMovement != null && dto != null) {
            if (dto.getItemId() != null) {
                stockMovement.setItem(new Item(dto.getItemId(), null));
            }

            if (dto.getQuantity() > 0) {
                stockMovement.setQuantity(dto.getQuantity());
                stockMovement.setQuantityAvailable(dto.getQuantity());
            }
        }
    }

    public static StockMovement fromDTO(StockMovementDTO dto) {
        if (dto != null) {
            StockMovement stockMovement = new StockMovement();
            stockMovement.setId(dto.getId());
            stockMovement.setCreationDate(dto.getCreationDate());
            merge(stockMovement, dto);
            return stockMovement;
        }
        return null;
    }

    public static StockMovementDTO toDTO(StockMovement stockMovement) {
        if (stockMovement != null) {
            StockMovementDTO dto = new StockMovementDTO();
            dto.setId(stockMovement.getId());
            dto.setCreationDate(stockMovement.getCreationDate());
            Item item = stockMovement.getItem();
            if (item != null) {
                dto.setItemId(item.getId());
            }
            dto.setQuantity(stockMovement.getQuantity());
            dto.setStatus(stockMovement.getStatus());
            dto.setQuantityAvailable(stockMovement.getQuantityAvailable());
            if (stockMovement.getOrderList()!=null) {
                dto.setOrderList(
                        stockMovement.getOrderList().stream()
                                .map((order) -> {
                                    order.setStockMovements(null);
                                    return Order.toDTO(order);
                                })
                                .collect(Collectors.toList())
                );
            }
            return dto;
        }
        return null;
    }
}
