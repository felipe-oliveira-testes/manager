package com.manager.service;

import com.manager.entity.Item;
import com.manager.entity.Order;
import com.manager.entity.OrderDTO;
import com.manager.entity.OrderStatus;
import com.manager.entity.User;
import com.manager.exception.ChangeNotAllowed;
import com.manager.exception.EntityNotFound;
import com.manager.repository.ItemRepository;
import com.manager.repository.OrderRepository;
import com.manager.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderProcessorService orderProcessorService;

    private static final Logger logger = LogManager.getLogger(OrderService.class);

    public List<OrderDTO> getOrders() {
        return orderRepository.findAll()
                .stream()
                .map(Order::toDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long id) throws EntityNotFound {
        Order order = getEntityByIdIfExists(id);
        return Order.toDTO(order);
    }

    public OrderDTO createOrder(OrderDTO orderDTO) throws ChangeNotAllowed {
        validationEntity(orderDTO);
        Order order = Order.fromDTO(orderDTO);
        order.setCreationDate(new Date());
        order.setStatus(OrderStatus.WAITING.name());
        order = orderRepository.save(order);
        // log order created
        logger.info(String.format("Order created to item %s and quantity %s",
                orderDTO.getItemId(), orderDTO.getQuantity()));
        // run async process to deliver orders
        orderProcessorService.processOrdersFromItem(orderDTO.getItemId());
        return Order.toDTO(order);
    }

    public OrderDTO updateOrder(Long id, OrderDTO orderDTO)
            throws ChangeNotAllowed, EntityNotFound {
        Order order = getEntityByIdIfExists(id);
        checkCanChangeOrder(order);
        validationEntity(orderDTO);
        Order.mergeWithDTO(order, orderDTO);
        order = orderRepository.save(order);
        // log order updated
        logger.info(String.format("Order updated to item %s and quantity %s",
                orderDTO.getItemId(), orderDTO.getQuantity()));
        // run async process to deliver orders
        orderProcessorService.processOrdersFromItem(order.getItem().getId());
        return Order.toDTO(order);
    }

    public void deleteOrder(Long id) throws EntityNotFound, ChangeNotAllowed {
        Order order = getEntityByIdIfExists(id);
        checkCanChangeOrder(order);
        orderRepository.deleteById(id);
    }

    private Order getEntityByIdIfExists(Long id) throws EntityNotFound {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            return optionalOrder.get();
        } else {
            throw new EntityNotFound(String.format("Order with id %s not found", id));
        }
    }

    private void checkCanChangeOrder(Order order) throws ChangeNotAllowed {
        if (OrderStatus.FINISHED.equals(OrderStatus.valueOf(order.getStatus()))) {
            throw new ChangeNotAllowed(String.format("Order with id %s is already finished", order.getId()));
        }
    }

    private void validationEntity(OrderDTO dto) throws ChangeNotAllowed {
        Optional<Item> optionalItem = itemRepository.findById(dto.getItemId());
        if (!optionalItem.isPresent()) {
            throw new ChangeNotAllowed(String.format("item %s invalid", dto.getItemId()));
        }

        Optional<User> optionalUser = userRepository.findById(dto.getUserId());
        if (!optionalUser.isPresent()) {
            throw new ChangeNotAllowed(String.format("user %s invalid", dto.getUserId()));
        }

        if (dto.getQuantity() <= 0) {
            throw new ChangeNotAllowed("quantity must be greater than 0");
        }
    }
}
