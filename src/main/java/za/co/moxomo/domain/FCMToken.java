package za.co.moxomo.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FCMToken {

    @Id
    public String id;
    private String token;
}

