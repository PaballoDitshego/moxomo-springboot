package za.co.moxomo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
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
    private String id;

    @NotNull
    private String keyword;

    private String mobileNumber;

    @NotNull
    private String gcmToken;

    @NotNull
    private boolean smsAlert;

    @NotNull
    private boolean pushAlert;


    @NotNull
    @Valid
    @JsonIgnore
    private Criteria criteria;

    @Value
    @Builder
    public static class Criteria {

        private String keyword;

        private String location;


        @GeoSpatialIndexed
        private double[] point;


        private String tags;




    }
}