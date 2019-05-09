package za.co.moxomo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class AlertDTO {
    private String alertId;
    private String location;
    private String tags;
    private boolean sms;
    private boolean push;
    private String title;
    private String mobileNumber;
    private String gcmToken;

}
