package za.co.moxomo.services;

import za.co.moxomo.domain.Notification;

public interface NotificationSendingService {

    void sendAlert(Notification notification);

    void sendAlert(String destination, Notification notification);

}
