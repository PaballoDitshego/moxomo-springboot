
package za.co.moxomo.crawlers.model.absa.response;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "facetValueResults",
    "levelList"
})
public class FacetResult {

    @JsonProperty("id")
    private String id;
    @JsonProperty("facetValueResults")
    private List<FacetValueResult> facetValueResults = null;
    @JsonProperty("levelList")
    private List<LevelList> levelList = null;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("facetValueResults")
    public List<FacetValueResult> getFacetValueResults() {
        return facetValueResults;
    }

    @JsonProperty("facetValueResults")
    public void setFacetValueResults(List<FacetValueResult> facetValueResults) {
        this.facetValueResults = facetValueResults;
    }

    @JsonProperty("levelList")
    public List<LevelList> getLevelList() {
        return levelList;
    }

    @JsonProperty("levelList")
    public void setLevelList(List<LevelList> levelList) {
        this.levelList = levelList;
    }

}
