package za.co.moxomo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import za.co.moxomo.enums.PushAlert;
import za.co.moxomo.enums.SmsAlert;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Document
public class AlertPreference {

    @Id
    private String alertPreferenceId;

    @NotNull
    private String title;

    private String mobileNumber;

    @NotNull
    private String gcmToken;

    @NotNull
    private SmsAlert smsAlert;

    @NotNull
    private PushAlert pushAlert;

    @NotNull
    @Valid
    private Criteria criteria;

    @Value
    @Builder
    public static class Criteria {

        private String jobTitle;

        private String[] province;

        private String[] town;

        private String[] tags;


    }
}