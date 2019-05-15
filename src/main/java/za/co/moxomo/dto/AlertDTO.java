package za.co.moxomo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertDTO {
    private String alertId;
    private String location;
    private boolean sms;
    private boolean push;
    private String keyword;
    private String mobileNumber;
    private String gcmToken;


}
