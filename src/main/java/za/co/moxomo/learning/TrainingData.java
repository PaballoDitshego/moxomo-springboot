
package za.co.moxomo.learning;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "content",
    "annotation"
})
public class TrainingData {

    @JsonProperty("content")
    private String content;
    @JsonProperty("annotation")
    private List<Annotation> annotation = null;

    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    @JsonProperty("content")
    public void setContent(String content) {
        this.content = content;
    }

    @JsonProperty("annotation")
    public List<Annotation> getAnnotation() {
        return annotation;
    }

    @JsonProperty("annotation")
    public void setAnnotation(List<Annotation> annotation) {
        this.annotation = annotation;
    }

}
