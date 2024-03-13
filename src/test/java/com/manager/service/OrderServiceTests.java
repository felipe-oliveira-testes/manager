package com.manager.service;

import com.manager.entity.Item;
import com.manager.entity.Order;
import com.manager.entity.OrderDTO;
import com.manager.entity.OrderStatus;
import com.manager.entity.User;
import com.manager.exception.ChangeNotAllowed;
import com.manager.exception.EntityNotFound;
import com.manager.repository.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class OrderServiceTests {

    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;

    private static final Long FINISHED_ORDER_ID = 1L;
    private static final Long WAITING_ORDER_ID = 2L;
    private static final Long WAITING_ORDER_ID_TO_UPDATE = 3L;
    private static final Long NOT_FOUND_ORDER_ID = 5L;
    private static final Long ITEM_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Date CREATION_DATE = new Date();
    private static final Long QUANTITY = 5L;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);

        // Prepare find scenario
        Order orderFinished = createOrder(FINISHED_ORDER_ID, OrderStatus.FINISHED.name());
        Mockito.when(orderRepository.findById(FINISHED_ORDER_ID))
                .thenReturn(Optional.of(orderFinished));

        // Prepare find scenario
        Order orderWaiting = createOrder(WAITING_ORDER_ID, OrderStatus.WAITING.name());
        Mockito.when(orderRepository.findById(WAITING_ORDER_ID))
                .thenReturn(Optional.of(orderWaiting));

        // Prepare findAll scenario
        List<Order> orders = new ArrayList<>();
        orders.add(orderFinished);
        orders.add(orderWaiting);
        Mockito.when(orderRepository.findAll())
                .thenReturn(orders);
    }

    @Test
    public void getOrdersByUserTest() {
        List<OrderDTO> result = orderService.getOrders();
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void getOrderByIdTest() {
        try {
            OrderDTO result = orderService.getOrderById(WAITING_ORDER_ID);
            Order orderWaiting = createOrder(WAITING_ORDER_ID, OrderStatus.WAITING.name());
            OrderDTO orderDTO = Order.toDTO(orderWaiting);
            Assertions.assertThat(result.getId()).isEqualTo(orderDTO.getId());
            Assertions.assertThat(result.getUserId()).isEqualTo(orderDTO.getUserId());
            Assertions.assertThat(result.getItemId()).isEqualTo(orderDTO.getItemId());
            Assertions.assertThat(result.getQuantity()).isEqualTo(orderDTO.getQuantity());
        } catch (EntityNotFound e) {
            Assertions.fail(String.format("%s should not be thrown", e.getClass().getName()));
        }
    }

    @Test
    public void updateOrderEntityNotFoundTest() {
        OrderDTO dto = createOrderDTO(NOT_FOUND_ORDER_ID);
        Assertions.assertThatThrownBy(
                () -> orderService.updateOrder(NOT_FOUND_ORDER_ID, dto)
        ).isInstanceOf(EntityNotFound.class);
    }

    @Test
    public void updateOrderChangeNotAllowedTest() {
        OrderDTO dto = createOrderDTO(FINISHED_ORDER_ID);
        Assertions.assertThatThrownBy(
                () -> orderService.updateOrder(FINISHED_ORDER_ID, dto)
        ).isInstanceOf(ChangeNotAllowed.class);
    }

    @Test
    public void deleteOrderTest() {
        Assertions.assertThatNoException()
                .isThrownBy(() -> orderService.deleteOrder(WAITING_ORDER_ID));
    }

    @Test
    public void deleteOrderEntityNotFoundTest() {
        Assertions.assertThatThrownBy(
                () -> orderService.deleteOrder(NOT_FOUND_ORDER_ID)
        ).isInstanceOf(EntityNotFound.class);
    }

    @Test
    public void deleteOrderChangeNotAllowedTest() {
        Assertions.assertThatThrownBy(
                () -> orderService.deleteOrder(FINISHED_ORDER_ID)
        ).isInstanceOf(ChangeNotAllowed.class);
    }

    private Order createOrder(Long id, String status) {
        Order order = new Order();
        order.setId(id);
        order.setCreationDate(CREATION_DATE);
        order.setItem(new Item(ITEM_ID, null));
        order.setUser(new User(USER_ID, null, null));
        order.setQuantity(QUANTITY);
        order.setStatus(status);
        return order;
    }

    private OrderDTO createOrderDTO(Long id) {
        OrderDTO order = new OrderDTO();
        order.setId(id);
        order.setCreationDate(CREATION_DATE);
        order.setItemId(ITEM_ID);
        order.setUserId(USER_ID);
        order.setQuantity(QUANTITY);
        return order;
    }
}
