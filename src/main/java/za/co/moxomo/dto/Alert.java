package za.co.moxomo.dto;

import lombok.Getter;
import lombok.Setter;
import za.co.moxomo.domain.AlertPreference;
import za.co.moxomo.enums.AlertType;

import java.time.Instant;

@Getter
@Setter
public class Alert {

    private String alertId;
    private AlertPreference alertPreference;
    private boolean delivered;
    private String message;
    private Instant createdDateTime;
    private String title;
    private String entityType;
    private int priority;
    private String actionType;
    private AlertType alertType;

}
