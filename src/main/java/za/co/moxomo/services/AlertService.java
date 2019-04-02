package za.co.moxomo.services;

import za.co.moxomo.domain.Alert;

public interface AlertService {

    void sendAlert(Alert alert);

    void sendAlert(String destination, Alert alert);

}
