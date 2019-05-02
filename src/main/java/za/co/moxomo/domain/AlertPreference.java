package za.co.moxomo.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@Document
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AlertPreference {

    @Id
    private String alertPreferenceId;

    @NotNull
    private String title;

    private String mobileNumber;

    @NotNull
    private String gcmToken;

    @NotNull
    private boolean smsAlert;

    @NotNull
    private boolean pushAlert;

    @NotNull
    @Valid
    private Criteria criteria;

    @Value
    @Builder
    public static class Criteria {

        private String jobTitle;

        private String location;


        private String tags;


    }
}