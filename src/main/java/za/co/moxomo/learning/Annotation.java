
package za.co.moxomo.learning;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "label",
    "points"
})
public class Annotation {

    @JsonProperty("label")
    private List<String> label = null;
    @JsonProperty("points")
    private List<Point> points = null;

    @JsonProperty("label")
    public List<String> getLabel() {
        return label;
    }

    @JsonProperty("label")
    public void setLabel(List<String> label) {
        this.label = label;
    }

    @JsonProperty("points")
    public List<Point> getPoints() {
        return points;
    }

    @JsonProperty("points")
    public void setPoints(List<Point> points) {
        this.points = points;
    }

}
