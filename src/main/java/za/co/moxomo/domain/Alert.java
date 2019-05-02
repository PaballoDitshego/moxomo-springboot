package za.co.moxomo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import za.co.moxomo.enums.AlertType;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
@Builder
@Data
public class Alert {

	@Id
	private String alertId;
	private String route;
	private String description;
	private Instant createdDateTime;
	private String imageUrl;
	private String url;
	private String location;
	private Date advertDate;
	private String title;
	private String entityType;
	private String entityId;
	private int priority;
	private String gcmToken;
	private String mobileNumber;
	private String alertType;

	private String actionType;

}
