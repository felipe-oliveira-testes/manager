package com.manager.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name="orders")
public class Order {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private Date creationDate;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
    private Item item;
    private long quantity;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
    private String status;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "orders_stock_movements",
            joinColumns = { @JoinColumn(name = "order_id") },
            inverseJoinColumns = { @JoinColumn(name = "stock_movements_id") }
    )
    private List<StockMovement> stockMovements;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<StockMovement> getStockMovements() {
        return stockMovements;
    }

    public void setStockMovements(List<StockMovement> stockMovements) {
        this.stockMovements = stockMovements;
    }

    public static void mergeWithDTO(Order order, OrderDTO dto) {
        if (order != null && dto != null) {
            if (dto.getItemId() != null) {
                order.setItem(new Item(dto.getItemId(), null));
            }

            if (dto.getUserId() != null) {
                order.setUser(new User(dto.getUserId(), null, null));
            }

            if (dto.getQuantity() > 0) {
                order.setQuantity(dto.getQuantity());
            }
        }
    }

    public static Order fromDTO(OrderDTO dto) {
        if (dto != null) {
            Order order = new Order();
            mergeWithDTO(order, dto);
            return order;
        }

        return null;
    }

    public static OrderDTO toDTO(Order order) {
        if (order != null) {
            OrderDTO dto = new OrderDTO();
            dto.setId(order.getId());
            dto.setCreationDate(order.getCreationDate());
            if (order.getUser() != null) {
                dto.setUserId(order.getUser().getId());
            }
            if (order.getItem() != null) {
                dto.setItemId(order.getItem().getId());
            }
            dto.setQuantity(order.getQuantity());
            dto.setStatus(order.getStatus());
            if (order.getStockMovements()!=null) {
                dto.setStockMovementDTOList(
                        order.getStockMovements().stream()
                                .map((item) -> {
                                    item.setOrderList(null);
                                    return StockMovement.toDTO(item);
                                })
                                .collect(Collectors.toList())
                );
            }
            return dto;
        }
        return null;
    }
}
