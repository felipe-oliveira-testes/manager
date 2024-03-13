package com.manager.service;

import com.manager.entity.Order;
import com.manager.entity.OrderStatus;
import com.manager.entity.StockMovement;
import com.manager.entity.StockMovementStatus;
import com.manager.repository.OrderRepository;
import com.manager.repository.StockMovementRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class OrderProcessorService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    StockMovementRepository stockMovementRepository;

    @Autowired
    NotificationSender notificationSender;

    Executor executor = Executors.newFixedThreadPool(1);

    private static final Logger logger = LogManager.getLogger(OrderProcessorService.class);

    public void processOrdersFromItem(Long itemId) {
        executor.execute(new Runnable(){
            public void run(){
                /*
                 * Get the list of orders waiting to be delivered
                 *  this way we will not process recent orders and skip existing orders
                 */
                List<Order> orderList = orderRepository
                        .findByItemIdAndStatusOrderByIdAsc(itemId,OrderStatus.WAITING.name());
                if (orderList != null && !orderList.isEmpty()) {
                    for (Order order : orderList) {
                        // get the list of stock movements available to deliver the order
                        List<StockMovement> stockMovementList = stockMovementRepository
                                .findAllByItemIdAndStatusIsNot(itemId, StockMovementStatus.FINISHED.name());
                        // if there is no stock movements available, we stop the interaction over orders
                        if (stockMovementList.isEmpty()) {
                            break;
                        }

                        // quantity of item the order need
                        long orderQuantity = order.getQuantity();
                        // list of stock movements used to deliver the order
                        List<StockMovement> movementsToDeliverOrder = new ArrayList<>();

                        // iterate over the stock movements trying to deliver the order
                        for (StockMovement stock : stockMovementList) {
                            long quantityAvailable = stock.getQuantityAvailable();
                            if (quantityAvailable >= orderQuantity) {
                                quantityAvailable -= orderQuantity;
                                orderQuantity = 0;
                            } else {
                                orderQuantity -= quantityAvailable;
                                quantityAvailable = 0;
                            }
                            // set the new quantity available
                            stock.setQuantityAvailable(quantityAvailable);
                            // set the stock movement status according to the quantity available
                            stock.setStatus(quantityAvailable > 0
                                    ? StockMovementStatus.PROCESSING.name()
                                    : StockMovementStatus.FINISHED.name());
                            // add the stock movement to the list of stocks used to deliver the order
                            movementsToDeliverOrder.add(stock);
                            // if we already have de order quantity, we stop the iteration
                            if (orderQuantity == 0) {
                                break;
                            }
                        }

                        // if the order is delivered, we will persist the stock movements changes
                        if (orderQuantity == 0) {
                            // update order
                            order.setStatus(OrderStatus.FINISHED.name());
                            order.setStockMovements(movementsToDeliverOrder);
                            order = orderRepository.save(order);
                            // update used stock movements
                            stockMovementRepository.saveAll(movementsToDeliverOrder);
                            // log order completed
                            logger.info(String.format("Order %s completed", order.getId()));
                            // send email
                            notificationSender.sendNotification(order);
                        } else {
                            // if the order is not delivered, we will not persist the change and the order will be waiting
                            break;
                        }
                    }
                }
            }
        });
    }

}
