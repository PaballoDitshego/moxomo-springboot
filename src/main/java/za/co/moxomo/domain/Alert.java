package za.co.moxomo.domain;

import lombok.Getter;
import lombok.Setter;
import za.co.moxomo.enums.AlertType;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Alert {

	@Id
	private String alertId;
	private AlertPreference alertPreference;
	private boolean delivered;
	private String message;
	private Instant createdDateTime;
	private String title;
	private String entityType;
	private String entityId;
	private int priority;
	private String actionType;
	private AlertType alertType;

}
