package com.manager.service;

import com.manager.entity.Order;

public interface NotificationSender {
    void sendNotification(Order order);
}
