package za.co.moxomo.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Document
@NoArgsConstructor

public class SearchSuggestionKeyword {

    @Id
    private String id;
    @Indexed
    @JsonProperty("keyword")
    private String keyword;

    public SearchSuggestionKeyword(String keyword) {
        this.keyword = keyword;
    }
}
